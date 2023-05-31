package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class InsertFotoIdentitasIndenBookingAsyncUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository,
): AsyncUseCase<InsertFotoIdentitasIndenBookingAsyncUseCase.Request, Nothing?>() {

    data class Request(val keyId: String, val uri: Uri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(imageDataDiriRepository.insertFromIndenBooking(request.keyId, request.uri))
        }
    }
}