package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.indenBooking.FotoPembayaranIndenBookingRepository

class DeleteFotoPembayaranIndenBookingAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranIndenBookingRepository,
): AsyncUseCase<DeleteFotoPembayaranIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val keyId: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(fotoPembayaranRepository.delete(request.keyId, request.termin))
        }
    }
}