package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.InsertPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class InsertPembayaranUseCaseTest {

    private val pembayaranRepository = mock<PembayaranRepository>()
    private val useCase = InsertPembayaranAsyncUseCase(pembayaranRepository)

    @Before
    fun initializeMocks() {
        whenever(pembayaranRepository.addPembayaran(
            kavlingKode = anyString(),
            pembayaran = any(),
        )).thenReturn(flowOf(Result.success(true)))
    }

    @Test
    fun whenJumlahUangDibayarLessOrEqualToZero_shouldError() {
        val kavling = "D1"
        val randomZeroOrNegativeNumber = Random.nextLong(from = -128L, until = 0)
        val pembayaran = Pembayaran(
            termin = "ITJ 1",
            tanggal = "1/1/2001",
            jumlahUangDibayar = NumberUtil.formatLongToString(randomZeroOrNegativeNumber),
            keterangan = "-",
            timeMillis = System.currentTimeMillis(),
        )

        runTest {
            val request = InsertPembayaranAsyncUseCase.KavlingRequest(kavling, pembayaran)
            useCase.execute(request).collect { result ->
                Assert.assertEquals(true, result.isFailure)
            }

            val validPembayaran = pembayaran.copy(jumlahUangDibayar = "1")
            val newRequest = InsertPembayaranAsyncUseCase.KavlingRequest(kavling, validPembayaran)
            useCase.execute(newRequest).collect { result ->
                Assert.assertEquals(true, result.isSuccess)
            }
        }
    }
}