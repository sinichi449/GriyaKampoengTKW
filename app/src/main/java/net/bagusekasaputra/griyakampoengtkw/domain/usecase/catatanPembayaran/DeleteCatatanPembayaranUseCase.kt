package net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteCatatanPembayaranUseCase @Inject constructor(
    private val catatanPembayaranRepository: CatatanPembayaranRepository,
): UseCase<DeleteCatatanPembayaranUseCase.Request, DeleteCatatanPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return catatanPembayaranRepository.deleteCatatan(request.kavlingKode).map {
            Response(it)
        }
    }
}