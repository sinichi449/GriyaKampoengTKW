package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetDataDiriIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository
): AsyncUseCase<GetDataDiriIndenBookingAsyncUseCase.Request, DataDiri>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<DataDiri?>> {
        return flow {
            val result = indenBookingRepository.getDataDiri(request.keyId, DataMode.ONLINE)

            emit(result)
        }
    }

}