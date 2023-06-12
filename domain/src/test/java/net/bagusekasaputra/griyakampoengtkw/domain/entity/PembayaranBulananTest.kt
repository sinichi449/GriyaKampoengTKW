package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.normalize
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.juta
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class PembayaranBulananTest {

    private val pembayaranRepository = MockRepository(getTestingFile(this)).getPembayaranRepository()

    @Test
    fun whenGroupingPembayaranListIntoPembayaranBulanan_shouldUseBulanAngsuranInsteadOfTanggalPembayaran() {
        runTest {
            val pembayaranListDeferred = CompletableDeferred<List<Pembayaran>?>()
            pembayaranRepository.getAllPembayaran("A8", DataMode.ONLINE).collect { result ->
                result.onSuccess {
                    pembayaranListDeferred.complete(it)
                }
                result.onFailure {
                    pembayaranListDeferred.completeExceptionally(it)
                }
            }

            val pembayaranListOriginal = pembayaranListDeferred.await()

            val dp1 = pembayaranListOriginal?.get(0)?.copy(
                tanggal = "28/12/2022", bulanAngsuran = BulanAngsuran(12, 2022)
            )
            val dp2 = pembayaranListOriginal?.get(1)?.copy(
                tanggal = "28/02/2023", bulanAngsuran = BulanAngsuran(1, 2023)
            )

            /*
            ~~~~~~~~~~~~~~~~~~~~~
            | Kav. A8 (Mutated) |
            ~~~~~~~~~~~~~~~~~~~~~

            No             Termin         Invoice        Tanggal
            --------------------------------------------------------
            1              DP 1           12/2022        28/12/2022
            2              DP 2           01/2023        28/02/2023
            3              ITJ 1          03/2023        14/03/2023
            4              Termin 1       04/2023        30/04/2023
            5              Termin 2       05/2023        31/05/2023
             */
            val pembayaranListMutated = pembayaranListOriginal?.toMutableList()
                ?.apply {
                    set(0, dp1!!)
                    set(1, dp2!!)
                }?.toList()
            val pembayaranBulananList = PembayaranBulanan.groupPembayaranIntoBulanan(
                kavling = "A8",
                baselinePembayaran = BaselinePembayaran(
                    kavling = "A8",
                    opsiBulan = 48,
                    jumlahUang = 5.0.juta(),
                    tanggalPembayaranMaks = 3,
                    timeMillis = System.currentTimeMillis(),
                ),
                pembayaranList = pembayaranListMutated!!,
            )

            val isExistPembayaranInTheMonthOf = { list: List<PembayaranBulanan>, bulan: Int, tahun: Int ->
                var isExist = false
                for (pembayaranBulanan in list) {
                    if (pembayaranBulanan.bulan == bulan && pembayaranBulanan.tahun == tahun) {
                        if (pembayaranBulanan.listPembayaran.isNotEmpty()) {
                            isExist = true
                            break
                        }
                    }
                }

                isExist
            }
            Assert.assertEquals(
                false, isExistPembayaranInTheMonthOf(pembayaranBulananList, 2, 2023)
            )
            Assert.assertEquals(
                true, isExistPembayaranInTheMonthOf(pembayaranBulananList, 5, 2023)
            )
        }
    }

    @Test
    fun whenPassedArgumentIsUnsortedPembayaranList_shouldKeepReturningOrderedPembayaranBulananList() {
        runTest {
            val kavling = "A11"
            val pembayaranA11Deferred = CompletableDeferred<List<Pembayaran>>()

            pembayaranRepository.getAllPembayaran(kavling, DataMode.ONLINE).collect { result ->
                result.onSuccess {
                    pembayaranA11Deferred.complete(it!!)
                }
                result.onFailure {
                    pembayaranA11Deferred.completeExceptionally(it)
                }
            }

            val pembayaranA11Original = pembayaranA11Deferred.await()
            val pembayaranA11Shuffled = pembayaranA11Original.shuffled()

            val pembayaranBulananList = PembayaranBulanan.groupPembayaranIntoBulanan(
                kavling = kavling,
                baselinePembayaran = BaselinePembayaran(
                    kavling = kavling,
                    opsiBulan = 48,
                    jumlahUang = 5_000_000L,
                    tanggalPembayaranMaks = 10,
                ),
                pembayaranList = pembayaranA11Shuffled
            )
            val bulanListPembayaranBulanan = pembayaranBulananList.run {
                buildList {
                    this@run.forEach {
                        add(it.bulanTahunDate)
                    }
                }
            }

            val bulanBeliKavlingA11 = pembayaranA11Original
                .sortedBy {
                    it.tanggal.toDate()
                }
                .first().tanggal.toDate()
                .run {
                    // set tanggal to tanggal 1
                    val calendar = Calendar.getInstance()
                    calendar.time = this
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                    calendar.normalize()

                    calendar.time
                }
            val bulanSekarang = Calendar.getInstance().time
            val fromBulanBeliUntilToday = DateUtil.getListMonths(
                dateFrom = bulanBeliKavlingA11,
                dateTo = bulanSekarang
            )

            Assert.assertEquals(true,
                bulanListPembayaranBulanan.isNotEmpty() && fromBulanBeliUntilToday.isNotEmpty()
            )
            Assert.assertEquals(true, bulanListPembayaranBulanan.containsAll(fromBulanBeliUntilToday))
        }
    }

    @Test
    fun givenPreviousMonthsHasNoPembayaranEntry_whenHitungAlokasi_shouldTunggakanRecursivelyIncreasing() {
        val kavling = "B3"
        val bulanSekarang = 6
        val tahunSekarang = 2023

        val pembayaranList = listOf(Pembayaran(
            termin = "ITJ 1",
            tanggal = "22/03/2023",
            jumlahUangDibayar = "2,000,000",
            keterangan = "-",
            timeMillis = System.currentTimeMillis(),
        ))
        val pembayaranBulanan = listOf(PembayaranBulanan(
            kavling = kavling,
            bulan = 3,
            tahun = 2023,
            listPembayaran = pembayaranList,
            baselinePembayaran = BaselinePembayaran.EMPTY(kavling),
        ))
    }

}