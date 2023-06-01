package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.indenBooking.FotoPembayaranIndenBookingRepository

class GetFotoPembayaranIndenBookingAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranIndenBookingRepository,
): AsyncUseCase<GetFotoPembayaranIndenBookingAsyncUseCase.Request, FotoPembayaranIndenBooking>() {

    data class Request(val keyId: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<FotoPembayaranIndenBooking?>> {
        return flow {
            emit(fotoPembayaranRepository.get(request.keyId, request.termin))
        }
    }
}