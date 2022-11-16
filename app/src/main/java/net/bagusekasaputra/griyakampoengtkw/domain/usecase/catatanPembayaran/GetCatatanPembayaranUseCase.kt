package net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCatatanPembayaranUseCase @Inject constructor(
    private val catatanPembayaranRepository: CatatanPembayaranRepository,
): UseCase<GetCatatanPembayaranUseCase.Request, GetCatatanPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<CatatanPembayaran?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return catatanPembayaranRepository.getCatatan(request.kavlingKode).map {
            Response(it)
        }
    }
}