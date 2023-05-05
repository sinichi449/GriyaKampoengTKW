package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetAllIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository
): AsyncUseCase<GetAllIndenBookingAsyncUseCase.Request, List<IndenBooking>>() {

    data class Request(val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<IndenBooking>?>> {
        return indenBookingRepository.getAll(request.dataMode)
    }
}