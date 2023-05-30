package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class GetDataDiriIndenBookingAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository
): AsyncUseCase<GetDataDiriIndenBookingAsyncUseCase.Request, DataDiri>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<DataDiri?>> {
        return flow {
            emit(dataDiriRepository.getFromIndenBooking(request.keyId))
        }
    }

}