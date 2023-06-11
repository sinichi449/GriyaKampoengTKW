package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndProgressStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository.Companion.DEFAULT_DATA_MODE
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class GetProgressKavlingUseCaseTest {

    private val mockRepository = MockRepository(getTestingFile(this))
    private val kavlingRepository = mockRepository.getKavlingRepository()
    private val pembayaranRepository = mockRepository.getPembayaranRepository()
    private val baselinePembayaranRepository = mockRepository.getBaselinePembayaranRepository()


    private val useCase = GetKavlingAndProgressStreamAsyncUseCase(
        kavlingRepository = kavlingRepository,
        pembayaranRepository = pembayaranRepository,
        baselineRepository = baselinePembayaranRepository,
    )

    private companion object {
        const val BULAN_INI = 6
        const val TAHUN_INI = 2023
    }

    @Test
    fun whenGetProgressKavling_shouldReturnBulanAngsuranInsteadOfTanggalPembayaran() {
        runTest {
            val blok = "A"
            val kavling = "A4"

            /*
            Kav. A4
            Angsuran    : 5,000,000

            No.            Termin         Invoice        Tanggal        Uang Dibayar
            ------------------------------------------------------------------------------------------
            1              ITJ 1          01/2023        01/01/2023     1,000,000
            2              DP 1           01/2023        30/01/2023     5,000,000
            3              DP 2           02/2023        05/02/2023     400,000
            4              DP 3           02/2023        02/03/2023     5,000,000
            5              DP 4           03/2023        01/04/2023     5,000,000
            6              DP 5           04/2023        30/04/2023     5,000,000
            7              DP 6           05/2023        01/06/2023     5,000,000
             */

            val request = GetKavlingAndProgressStreamAsyncUseCase.Request(
                blok = blok,
                dataMode = DEFAULT_DATA_MODE,
                bulanAngsuran = BULAN_INI,
                tahunAngsuran = TAHUN_INI,
            )
            var progressKavlingList = emptyList<KavlingAndProgress>()
            useCase.execute(request).collect { result ->
                result.onFailure {
                    throw it
                }
                result.onSuccess {
                    if (it.isNullOrEmpty()) {
                        throw Exception("KavlingAndProgress List is empty!")
                    } else {
                        progressKavlingList = it
                    }
                }
            }

            val kavlingAndProgress = progressKavlingList.first { item ->
                item.kavling.kode == kavling
            }
            val progress = kavlingAndProgress.progress.persentaseBulanIni()

            Assert.assertEquals(0, progress)
        }
    }
}