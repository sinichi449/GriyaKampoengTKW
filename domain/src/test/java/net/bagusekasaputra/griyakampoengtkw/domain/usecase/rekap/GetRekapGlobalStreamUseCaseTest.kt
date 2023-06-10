package net.bagusekasaputra.griyakampoengtkw.domain.usecase.rekap

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapGlobalStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.sortByTermin
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.tanggalPembelian
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository.Companion.DEFAULT_DATA_MODE
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class GetRekapGlobalStreamUseCaseTest {

    private val testingFile = getTestingFile(this)

    private val mockRepository = MockRepository(testingFile)
    private val dataDiriRepository = mockRepository.getDataDiriRepository()
    private val pembayaranRepository = mockRepository.getPembayaranRepository()
    private val hargaKavlingRepository = mockRepository.getHargaKavlingRepository()

    private val kavlingKodeList = mockRepository.getKavlingKodeList()

    private val useCase = GetRekapGlobalStreamAsyncUseCase(
        dataDiriRepository, pembayaranRepository, hargaKavlingRepository
    )

    @Test
    fun whenQueryingPembayaranList_shouldEqualToTanggalFirstPembayaran() = runTest {
        val correctTglMap = buildMap<String, Date?> {
            kavlingKodeList.forEach { kavling ->
                val pembayaranList = pembayaranRepository.getAllPembayaran(kavling, DEFAULT_DATA_MODE)
                    .first()
                    .onFailure { throw it }
                    .getOrNull()
                val sortedPembayaran = pembayaranList?.sortByTermin()
                if (!sortedPembayaran.isNullOrEmpty()) {
                    val tglPembelian = sortedPembayaran.tanggalPembelian().toDate()

                    put(kavling, tglPembelian)
                } else {
                    put(kavling, null)
                }
            }
        }

        val request = GetRekapGlobalStreamAsyncUseCase.Request(kavlingKodeList)
        val resultTglMap = mutableMapOf<String, Date?>()
        useCase.execute(request).collect { result ->
            result.onFailure { throw it }
            result.onSuccess {
                if (!it.isNullOrEmpty()) {
                    it.forEach { item ->
                        resultTglMap[item.noKavling] = item.tanggalPembelian
                    }
                }
            }
        }

        Assert.assertEquals(correctTglMap, resultTglMap)
    }
}