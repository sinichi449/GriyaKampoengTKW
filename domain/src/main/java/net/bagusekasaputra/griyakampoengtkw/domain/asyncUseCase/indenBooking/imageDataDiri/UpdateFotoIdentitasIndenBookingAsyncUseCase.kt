package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class UpdateFotoIdentitasIndenBookingAsyncUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository
): AsyncUseCase<UpdateFotoIdentitasIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val keyId: String, val uri: Uri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(imageDataDiriRepository.updateFromIndenBooking(request.keyId, request.uri))
        }
    }
}