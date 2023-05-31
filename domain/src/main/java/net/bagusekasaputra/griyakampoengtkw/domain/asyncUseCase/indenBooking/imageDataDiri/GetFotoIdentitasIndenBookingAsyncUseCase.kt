package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class GetFotoIdentitasIndenBookingAsyncUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository
): AsyncUseCase<GetFotoIdentitasIndenBookingAsyncUseCase.Request, Uri>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Uri?>> {
        return flow {
            emit(imageDataDiriRepository.getFromIndenBooking(request.keyId))
        }
    }

}