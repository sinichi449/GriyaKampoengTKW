package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.juta
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test

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

}