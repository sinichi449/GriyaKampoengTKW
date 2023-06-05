package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.UpdatePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class UpdatePembayaranUseCaseTest {

    private val pembayaranRepository = Mockito.mock(PembayaranRepository::class.java)

    @Test
    fun pembayaranKavling_whenTerminAreNotTheSame_shouldReturnError() {
        runBlocking {
            val oldPembayaran = Pembayaran(
                termin = "ITJ 1",
                tanggal = "5/6/2023",
                jumlahUangDibayar = "0",
                keterangan = anyString(),
                timeMillis = System.currentTimeMillis(),
            )
            val newPembayaran = oldPembayaran.copy(termin = "DP 1")
            val useCase = getUseCase(pembayaranRepository)
            val request = UpdatePembayaranAsyncUseCase.KavlingRequest(
                anyString(), oldPembayaran, newPembayaran
            )

            val result = useCase.execute(request).first()

            Assert.assertEquals(true, result.isFailure)
        }
    }

    @Test
    fun pembayaranKavling_whenPembayaranAreTheSame_shouldReturnError() {
        runBlocking {
            val pembayaran = Pembayaran(
                termin = "ITJ 1",
                tanggal = "5/6/2023",
                jumlahUangDibayar = "0",
                keterangan = anyString(),
                timeMillis = System.currentTimeMillis(),
            )
            val useCase = getUseCase(pembayaranRepository)
            val request = UpdatePembayaranAsyncUseCase.KavlingRequest(
                anyString(), pembayaran, pembayaran
            )

            val result = useCase.execute(request).first()

            Assert.assertEquals(true, result.isFailure)
        }
    }

    private fun getUseCase(pembayaranRepository: PembayaranRepository): UpdatePembayaranAsyncUseCase {
        return UpdatePembayaranAsyncUseCase(pembayaranRepository)
    }
}