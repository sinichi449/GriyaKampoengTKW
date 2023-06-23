package net.bagusekasaputra.griyakampoengtkw.domain.entity

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.normalize
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.FILTER_USING_BULAN_ANGSURAN
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.groupByTermins
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.sortByTermin
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.totalUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.juta
import net.bagusekasaputra.griyakampoengtkw.domain.model.PembayaranJson
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockHargaRumahIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository.Companion.DEFAULT_DATA_MODE
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar


@OptIn(ExperimentalCoroutinesApi::class)
class PembayaranTest {

    private val pembayaranRepository = MockPembayaranRepository()
    private val hargaRumahRepository = MockHargaRumahIndenBookingRepository()

    private val mockRepository = MockRepository(getTestingFile(this))
    private val mockPembayaranRepository = mockRepository.getPembayaranRepository()


    @Test
    fun givenJenisPembayaran_shouldReturnNextUrutanTermin() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val requestedJenisPembayaran = Pembayaran.JenisPembayaran.DP
        val correctNextSequence = "9"

        val pembayarans = runBlocking {
            pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
        }
        val nextSequence = Pembayaran.nextPembayaranSequence(pembayarans, requestedJenisPembayaran)

        assert(correctNextSequence == nextSequence)
    }

    @Test
    fun hitung_total_uang_masuk_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val correctTotalUangMasuk = 38_005_000L

        val pembayarans = runBlocking {
            pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
        }
        val totalUangMasuk = Pembayaran.hitungTotalUangMasuk(pembayarans)

        assert(totalUangMasuk == correctTotalUangMasuk)
    }

    @Test
    fun maskingPembayaranIndenBooking_shouldInCorrectOrder() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val orderedListTermin = listOf("ITJ 1", "DP 1", "DP 2", "DP 3", "DP 4", "DP 5",
            "DP 6", "DP 7", "DP 8")

        val sortedPembayaran = runBlocking {
            val pembayarans = pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
            val hargaRumah = hargaRumahRepository.get(keyId, DataMode.ONLINE).getOrThrow()!!

            val shuffledPembayaran = pembayarans.shuffled()

            Pembayaran.maskPembayaran(shuffledPembayaran, hargaRumah,
                onCekFotoPembayaran = { false },
                onCekSudahAmbilKuitansi = { false },
            )
        }
        val listTermin = sortedPembayaran.map { it.termin }

        Assert.assertEquals(orderedListTermin, listTermin)
    }

    @Test
    fun persentase_pembayaran_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val persentasePembayaranTerakhir = BigDecimal(0.135732143)
            .setScale(4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100L))
            .toDouble()

        val maskedPembayaran = runBlocking {
            val pembayarans = pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()
            val hargaRumah = hargaRumahRepository.get(keyId, DataMode.ONLINE).getOrThrow()

            Pembayaran.maskPembayaran(pembayarans!!, hargaRumah!!,
                onCekFotoPembayaran = { false },
                onCekSudahAmbilKuitansi = { false },
            )
        }
        val lastPembayaran = maskedPembayaran.last()

        assert(lastPembayaran.presentase == persentasePembayaranTerakhir)
    }

    @Test
    fun filterPeriodeBulanIni_filterUsingTanggal_shouldCorrect() {
        val calendar = Calendar.getInstance()
        val bulanSekarang = calendar.get(Calendar.MONTH) + 1
        val tahunSekarang = calendar.get(Calendar.YEAR)
        val totalPembayaran = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val pembayaranList = mutableListOf<Pembayaran>().apply {
            val startDate = "1/1/2020".toDate()
            val endDate = "${totalPembayaran}/$bulanSekarang/$tahunSekarang".toDate()

            val rangeTanggal = DateUtil.getListDate(startDate, endDate)
            rangeTanggal.forEach {
                add(Pembayaran(
                    termin = "",
                    tanggal = it.toSlashedString(),
                    jumlahUangDibayar = NumberUtil.formatLongToString(0L),
                    keterangan = "",
                    timeMillis = System.currentTimeMillis(),
                ))
            }
        }
        val bulanIniPembayaranList = pembayaranList.filterPeriode(PeriodeRekap.BULAN_INI, null, null)!!

        Assert.assertEquals(totalPembayaran, bulanIniPembayaranList.size)
    }

    @Test
    fun filterPeriodeMingguIni_filterUsingTanggal_shouldCorrect() {
        val calendar = Calendar.getInstance()
        val startDate = calendar.run {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            time
        }
        val endDate = calendar.run {
            add(Calendar.DAY_OF_WEEK, 7)

            time
        }
        val rangeDate = DateUtil.getListDate(startDate, endDate)

        val pembayaranList = mutableListOf<Pembayaran>().apply {
            rangeDate.forEach {
                add(Pembayaran(
                    termin = "",
                    tanggal = it.toSlashedString(),
                    jumlahUangDibayar = NumberUtil.formatLongToString(0L),
                    keterangan = "",
                    timeMillis = System.currentTimeMillis(),
                ))
            }
        }
        val mingguIniPembayaran = pembayaranList.filterPeriode(PeriodeRekap.MINGGU_INI, null, null)!!

        Assert.assertEquals(rangeDate.size, mingguIniPembayaran.size)
    }

    @Test
    fun filterPeriodeCustom_filterUsingBulanAngsuran_shouldCorrect() {
        val jsonPembayaran = "{\"DP 1\":{\"fullTermin\":\"DP 1\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"Dp 1 Januari \",\"tanggal\":\"30/01/2023\",\"termin\":\"DP\",\"timeMillis\":1675082135719,\"urutan\":1,\"invoiceDateStr\":\"01/2023\"},\"DP 2\":{\"fullTermin\":\"DP 2\",\"jumlahUangDibayar\":400000,\"keterangan\":\"Dp 2 = TF 500 - biaya pengiriman 100 RB \",\"tanggal\":\"05/02/2023\",\"termin\":\"DP\",\"timeMillis\":1675661757873,\"urutan\":2,\"invoiceDateStr\":\"02/2023\"},\"DP 3\":{\"fullTermin\":\"DP 3\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"Dp 3 February \",\"tanggal\":\"02/03/2023\",\"termin\":\"DP\",\"timeMillis\":1677727097661,\"urutan\":3,\"invoiceDateStr\":\"02/2023\"},\"DP 4\":{\"fullTermin\":\"DP 4\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"Dp 4 Maret\",\"tanggal\":\"01/04/2023\",\"termin\":\"DP\",\"timeMillis\":1680322761479,\"urutan\":4,\"invoiceDateStr\":\"03/2023\"},\"DP 5\":{\"fullTermin\":\"DP 5\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"Pemindahan dari B19 - A3\",\"tanggal\":\"30/04/2023\",\"termin\":\"DP\",\"timeMillis\":1682846004870,\"urutan\":5,\"invoiceDateStr\":\"04/2023\"},\"DP 6\":{\"fullTermin\":\"DP 6\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"Dp 6 Bulan Mei\",\"tanggal\":\"01/06/2023\",\"termin\":\"DP\",\"timeMillis\":1685589258226,\"urutan\":6,\"invoiceDateStr\":\"05/2023\"},\"ITJ 1\":{\"fullTermin\":\"ITJ 1\",\"jumlahUangDibayar\":1000000,\"keterangan\":\"ITJ 1 \",\"tanggal\":\"01/01/2023\",\"termin\":\"ITJ\",\"timeMillis\":1672566322153,\"urutan\":1,\n\"invoiceDateStr\": \"01/2023\"}}"
        val terminNodes = JsonParser.parseString(jsonPembayaran)
            .asJsonObject

        val pembayaranList = buildList {
            terminNodes?.keySet()?.forEach { termin ->
                val pembayaranNode = terminNodes.get(termin)
                pembayaranNode?.also {
                    val pembayaranJson = Gson().fromJson(it, PembayaranJson::class.java)
                    pembayaranJson?.toDomain()?.also { pembayaran ->
                        add(pembayaran)
                    }
                }
            }
        }

        val filterJuni = pembayaranList.filterPeriode(
            periode = PeriodeRekap.CUSTOM,
            start = "30/06/2023".toDate(),
            end = "01/06/2023".toDate(),
            filterMode = FILTER_USING_BULAN_ANGSURAN,
        )!!
        Assert.assertEquals(true, filterJuni.isEmpty())

        val filterMei = pembayaranList.filterPeriode(
            periode = PeriodeRekap.CUSTOM,
            start = "01/05/2023".toDate(),
            end = "31/05/2023".toDate(),
            filterMode = FILTER_USING_BULAN_ANGSURAN,
        )!!
        Assert.assertEquals(false, filterMei.isEmpty())
    }

    @Test
    fun whenCreatingBulanAngsuran_tanggalShouldSetToFirstDayInMonthAndNormalized() {
        val calendar = Calendar.getInstance()
        val tanggalSekarang = calendar.time.toSlashedString()
        val pembayaran = Pembayaran(
            termin = "Termin 5",
            tanggal = tanggalSekarang,
            jumlahUangDibayar = "0",
            keterangan = "-",
            timeMillis = System.currentTimeMillis(),
        )

        val firstDayInMonth = with(calendar) {
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
            normalize()

            time
        }
        val tanggalBulanAngsuran = pembayaran.bulanAngsuran.date

        Assert.assertEquals(firstDayInMonth, tanggalBulanAngsuran)
    }

    @Test
    fun whenBulanAngsuranIsNotSpecified_shouldDefaultToTanggalPembayaran() {
        val tanggalPembayaran = "08/06/2023"
        val pembayaran = Pembayaran(
            termin = "",
            tanggal = tanggalPembayaran,
            jumlahUangDibayar = "0",
            keterangan = "-",
            timeMillis = System.currentTimeMillis(),
        )
        val bulanAngsuran = pembayaran.bulanAngsuran

        Assert.assertEquals("06/2023", bulanAngsuran.bulanAndTahun)
    }

    @Test
    fun whenGetUangMasukBulanIni_shouldReturnTanggalBulanAngsuran() {
        val pembayaranList = buildList {
            add(Pembayaran(
                termin = "ITJ 1",
                tanggal = "04/01/2023",
                jumlahUangDibayar = NumberUtil.formatLongToString(5.0.juta()),
                keterangan = "-",
                timeMillis = System.currentTimeMillis(),
            ))
            add(Pembayaran(
                termin = "DP 1",
                tanggal = "04/02/2023",
                jumlahUangDibayar = NumberUtil.formatLongToString(5.0.juta()),
                keterangan = "-",
                timeMillis = System.currentTimeMillis(),
            ))
            add(Pembayaran(
                termin = "DP 2",
                tanggal = "04/04/2023",
                jumlahUangDibayar = NumberUtil.formatLongToString(5.0.juta()),
                keterangan = "DP 2 bulan Maret",
                timeMillis = System.currentTimeMillis(),
                bulanAngsuran = BulanAngsuran(3, 2023),
            ))
            add(Pembayaran(
                termin = "DP 3",
                tanggal = "04/05/2023",
                jumlahUangDibayar = NumberUtil.formatLongToString(5.0.juta()),
                keterangan = "DP 3 April",
                timeMillis = System.currentTimeMillis(),
                bulanAngsuran = BulanAngsuran(4, 2023),
            ))
            add(Pembayaran(
                termin = "DP 4",
                tanggal = "04/05/2023",
                jumlahUangDibayar = NumberUtil.formatLongToString(5.0.juta()),
                keterangan = "-",
                timeMillis = System.currentTimeMillis(),
            ))
        }

        val uangMasukMaret = Pembayaran.uangMasukPadaBulanDanTahunIni(
            pembayaranList, 3, 2023, FILTER_USING_BULAN_ANGSURAN
        )
        val uangMasukApril = Pembayaran.uangMasukPadaBulanDanTahunIni(
            pembayaranList, 4, 2023, FILTER_USING_BULAN_ANGSURAN
        )
        val uangMasukMei = Pembayaran.uangMasukPadaBulanDanTahunIni(
            pembayaranList, 5, 2023, FILTER_USING_BULAN_ANGSURAN
        )

        Assert.assertEquals(5_000_000L, uangMasukMaret)
        Assert.assertEquals(5_000_000L, uangMasukApril)
        Assert.assertEquals(5_000_000L, uangMasukMei)
    }

    @Test
    fun givenOrderedOrShuffledPembayaranList_whenHitungTotalUangMasuk_shouldReturnTheSame() = runTest {
        val kavling = "A11"
        val sortedPembayaran = pembayaranRepository.getAllPembayaran(kavling, DEFAULT_DATA_MODE)
            .first()
            .getOrThrow()
            ?.sortByTermin()

        val shuffledPembayaran = sortedPembayaran?.shuffled()

        val sortedUangMasuk = sortedPembayaran?.totalUangMasuk()
        val shuffledUangMasuk = shuffledPembayaran?.totalUangMasuk()

        Assert.assertEquals(sortedUangMasuk, shuffledUangMasuk)
    }

    @Test
    fun groupByJenisTerminTest() = runTest {
        val kavling = "A11"
        val pembayaranList = pembayaranRepository.getAllPembayaran(kavling, DEFAULT_DATA_MODE)
            .first().getOrThrow()?.toMutableList()

        // Above `pembayaranList` has no `Termin`. Add manually here.
        pembayaranList?.add(
            Pembayaran(
                termin = "Termin 1",
                tanggal = "23/06/2023",
                jumlahUangDibayar = "7,500,000",
                keterangan = "-",
                timeMillis = System.currentTimeMillis(),
            )
        )

        // `pembayaranList` has 1 ITJ, 8 DP, and 1 Termin
        val terminList = listOf("ITJ", "DP", "Termin")
        val grouped = pembayaranList?.groupByTermins(terminList)
        Assert.assertEquals(grouped?.get("ITJ")?.size, 1)
        Assert.assertEquals(grouped?.get("DP")?.size, 8)
        Assert.assertEquals(grouped?.get("Termin")?.size, 1)
    }
}