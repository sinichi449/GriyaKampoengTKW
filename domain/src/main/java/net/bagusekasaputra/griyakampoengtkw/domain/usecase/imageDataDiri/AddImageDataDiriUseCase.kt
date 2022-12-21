package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class AddImageDataDiriUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository,
): UseCase<AddImageDataDiriUseCase.Request, AddImageDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Boolean?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return imageDataDiriRepository.addImage(request.kavlingKode, request.uri).map {
            Response(it)
        }
    }
}