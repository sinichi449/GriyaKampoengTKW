package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetHargaRumahIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository
): AsyncUseCase<GetHargaRumahIndenBookingAsyncUseCase.Request, HargaRumahIndenBooking>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<HargaRumahIndenBooking?>> {
        return flow {
            val result = indenBookingRepository.getHargaRumah(request.keyId, DataMode.ONLINE)

            emit(result)
        }
    }
}