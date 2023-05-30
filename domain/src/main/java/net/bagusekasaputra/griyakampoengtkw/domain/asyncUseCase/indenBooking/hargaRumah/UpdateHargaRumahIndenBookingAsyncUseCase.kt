package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaRumahIndenBookingRepository

class UpdateHargaRumahIndenBookingAsyncUseCase(
    private val hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository,
): AsyncUseCase<UpdateHargaRumahIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(
        val keyId: String,
        val newHargaRumah: HargaRumahIndenBooking
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(hargaRumahIndenBookingRepository.update(request.keyId, request.newHargaRumah))
        }
    }
}