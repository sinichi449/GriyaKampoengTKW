package net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSingleHargaKavlingForPembayaranUseCase @Inject constructor(
    private val hargaKavlingRepository: HargaKavlingRepository
): UseCase<GetSingleHargaKavlingForPembayaranUseCase.Request, GetSingleHargaKavlingForPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val harga: Long): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return hargaKavlingRepository.getSingleHargaKavlingForPembayaran(request.kavlingKode).map {
            Response(it)
        }
    }
}