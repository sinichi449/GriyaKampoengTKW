package net.bagusekasaputra.griyakampoengtkw.domain.usecase.rekap

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class GetListRekapGlobalUseCaseTest {

    private val blockRepository = mock<BlockRepository>()
    private val kavlingRepository = mock<KavlingRepository>()

    private val testingFile = getTestingFile(this)

    private val mockRepository = MockRepository(testingFile)
    private val dataDiriRepository = mockRepository.getDataDiriRepository()
    private val pembayaranRepository = mockRepository.getPembayaranRepository()
    private val hargaKavlingRepository = mockRepository.getHargaKavlingRepository()

    private val kavlingKodeList = mockRepository.getKavlingKodeList()

    private val useCase = GetListRekapGlobalAsyncUseCase(
        blockRepository, kavlingRepository, dataDiriRepository,
        pembayaranRepository, hargaKavlingRepository
    )

}