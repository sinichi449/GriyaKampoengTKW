package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class EditIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository,
): AsyncUseCase<EditIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(
        val oldData: IndenBooking,
        val newData: IndenBooking,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return indenBookingRepository.update(
            oldData = request.oldData,
            newData = request.newData,
        )
    }
}