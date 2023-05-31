package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class DeleteFotoIdentitasIndenBookingAsyncUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository,
): AsyncUseCase<DeleteFotoIdentitasIndenBookingAsyncUseCase.Request, Nothing>() {

    data class Request(val keyId: String, val uri: Uri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(imageDataDiriRepository.deleteFromIndenBooking(request.keyId, request.uri))
        }
    }
}