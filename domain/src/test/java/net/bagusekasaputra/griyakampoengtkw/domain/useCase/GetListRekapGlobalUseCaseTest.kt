package net.bagusekasaputra.griyakampoengtkw.domain.useCase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockHargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockPembayaranRepository
import org.junit.Assert
import org.junit.Test

class GetListRekapGlobalUseCaseTest {

    private val dataDiriRepository = MockDataDiriRepository()
    private val hargaKavlingRepository = MockHargaKavlingRepository()
    private val pembayaranRepository = MockPembayaranRepository()

    @Test
    fun tanggalPembelian_shouldCorrect() {
        val kavlingList = listOf("A11")
        val useCase = GetListRekapGlobalAsyncUseCase(
            dataDiriRepository, pembayaranRepository, hargaKavlingRepository
        )

        val rekapGlobalList = runBlocking {
            val request = GetListRekapGlobalAsyncUseCase.Request(kavlingList)

            useCase.execute(request).first().getOrThrow()!!
        }
        val rekapGlobal = rekapGlobalList[0]

        Assert.assertEquals("09/12/2022", rekapGlobal.tanggalPembelian)
    }

    @Test
    fun jumlahUangMasuk_shouldCorrect() {
        val kavlingList = listOf("A11")
        val useCase = GetListRekapGlobalAsyncUseCase(
            dataDiriRepository, pembayaranRepository, hargaKavlingRepository
        )

        val rekapGlobalList = runBlocking {
            val request = GetListRekapGlobalAsyncUseCase.Request(kavlingList)

            useCase.execute(request).first().getOrThrow()!!
        }
        val rekapGlobal = rekapGlobalList[0]

        Assert.assertEquals(38_005_000L, rekapGlobal.jumlahUangMasuk)
    }
}