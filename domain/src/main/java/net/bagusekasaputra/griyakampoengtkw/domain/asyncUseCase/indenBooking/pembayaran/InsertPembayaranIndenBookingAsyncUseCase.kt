package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class InsertPembayaranIndenBookingAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<InsertPembayaranIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val keyId: String, val pembayaran: Pembayaran): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(pembayaranRepository.insertFromIndenBooking(request.keyId, request.pembayaran))
        }
    }
}