package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePembayaranByTerminUseCase @Inject constructor(
    private val pembayaranRepository: PembayaranRepository
): UseCase<DeletePembayaranByTerminUseCase.Request, DeletePembayaranByTerminUseCase.Response>() {

    data class Request(val kavlingKode: String, val termin: String): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.deletePembayaranByTermin(request.kavlingKode, request.termin).map {
            Response(it)
        }
    }
}