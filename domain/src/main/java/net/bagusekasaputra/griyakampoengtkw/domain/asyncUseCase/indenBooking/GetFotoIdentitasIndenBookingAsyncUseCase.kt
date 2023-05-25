package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetFotoIdentitasIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository,
): AsyncUseCase<GetFotoIdentitasIndenBookingAsyncUseCase.Request, Uri>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Uri?>> {
        return flow {
            val result = indenBookingRepository.getFotoIdentitas(request.keyId, DataMode.ONLINE)

            emit(result)
        }
    }

}