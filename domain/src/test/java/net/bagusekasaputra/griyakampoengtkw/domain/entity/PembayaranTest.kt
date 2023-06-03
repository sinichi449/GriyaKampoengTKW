package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockHargaRumahIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockPembayaranRepository
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar


class PembayaranTest {

    private val pembayaranRepository = MockPembayaranRepository()
    private val hargaRumahRepository = MockHargaRumahIndenBookingRepository()

    @Test
    fun total_uang_masuk_on_specified_bulan_and_tahun() {
        val kavling = "A11"
        val bulan = 12
        val tahun = 2022

        val uangMasuk = runBlocking {
            pembayaranRepository.getUangMasukBulanIni(kavling, bulan, tahun, DataMode.ONLINE)
                .getOrThrow()
        }

        Assert.assertEquals(25_375_000L, uangMasuk)
    }

    @Test
    fun sudah_bayar_pembayaran_on_specified_bulan_and_tahun() {
        val kavling = "A11"
        val bulan = 12
        val tahun = 2022

        val adaPembayaran = runBlocking {
            pembayaranRepository.sudahBayarAngsuran(kavling, bulan, tahun, DataMode.ONLINE)
                .getOrThrow()
        }

        assert(adaPembayaran == true)
    }

    @Test
    fun next_sequence_of_pembayaran_given_jenis_pembayaran_correct() {
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
    fun filterPeriodeBulanIni_shouldCorrect() {
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
                    termin = ArgumentMatchers.anyString(),
                    tanggal = it.toSlashedString(),
                    jumlahUangDibayar = NumberUtil.formatLongToString(ArgumentMatchers.anyLong()),
                    keterangan = ArgumentMatchers.anyString(),
                    timeMillis = System.currentTimeMillis(),
                ))
            }
        }
        val bulanIniPembayaranList = pembayaranList.filterPeriode(PeriodeRekap.BULAN_INI, null, null)!!

        Assert.assertEquals(totalPembayaran, bulanIniPembayaranList.size)
    }

    @Test
    fun filterPeriodeMingguIni_shouldCorrect() {
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
                    termin = ArgumentMatchers.anyString(),
                    tanggal = it.toSlashedString(),
                    jumlahUangDibayar = NumberUtil.formatLongToString(ArgumentMatchers.anyLong()),
                    keterangan = ArgumentMatchers.anyString(),
                    timeMillis = System.currentTimeMillis(),
                ))
            }
        }
        val mingguIniPembayaran = pembayaranList.filterPeriode(PeriodeRekap.MINGGU_INI, null, null)!!

        Assert.assertEquals(rangeDate.size, mingguIniPembayaran.size)
    }


}