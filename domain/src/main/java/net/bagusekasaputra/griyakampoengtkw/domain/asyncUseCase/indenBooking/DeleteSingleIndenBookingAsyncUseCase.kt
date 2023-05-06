package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class DeleteSingleIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository
): AsyncUseCase<DeleteSingleIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val indenBooking: IndenBooking): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return indenBookingRepository.delete(request.indenBooking)
    }
}