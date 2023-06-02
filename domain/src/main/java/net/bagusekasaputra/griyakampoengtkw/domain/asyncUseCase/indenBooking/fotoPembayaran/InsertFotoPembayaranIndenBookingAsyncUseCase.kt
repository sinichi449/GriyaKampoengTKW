package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranIndenBookingRepository

class InsertFotoPembayaranIndenBookingAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranIndenBookingRepository,
): AsyncUseCase<InsertFotoPembayaranIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val fotoPembayaran: FotoPembayaranIndenBooking): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(fotoPembayaranRepository.insert(request.fotoPembayaran))
        }
    }
}