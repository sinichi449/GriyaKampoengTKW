package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaRumahIndenBookingRepository

class GetHargaRumahIndenBookingAsyncUseCase(
    private val hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository,
): AsyncUseCase<GetHargaRumahIndenBookingAsyncUseCase.Request, HargaRumahIndenBooking>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<HargaRumahIndenBooking?>> {
        return flow {
            val result = hargaRumahIndenBookingRepository
                .get(request.keyId, DataMode.ONLINE)

            emit(result)
        }
    }
}