package net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetSingleHargaKavlingForPembayaranUseCase(
    private val hargaKavlingRepository: HargaKavlingRepository
): UseCase<GetSingleHargaKavlingForPembayaranUseCase.Request, GetSingleHargaKavlingForPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val hargaKavling: HargaKavling): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return hargaKavlingRepository.getSingleHargaKavlingForPembayaran(request.kavlingKode).map {
            Response(it)
        }
    }
}