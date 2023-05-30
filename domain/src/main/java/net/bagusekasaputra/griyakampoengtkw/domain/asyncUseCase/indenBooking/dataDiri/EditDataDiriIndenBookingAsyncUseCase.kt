package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class EditDataDiriIndenBookingAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
): AsyncUseCase<EditDataDiriIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(
        val keyId: String,
        val newDataDiri: DataDiri,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(dataDiriRepository.updateFromIndenBooking(request.keyId, request.newDataDiri))
        }
    }
}