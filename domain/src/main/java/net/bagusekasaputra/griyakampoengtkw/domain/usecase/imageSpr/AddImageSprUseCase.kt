package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import java.io.File

class AddImageSprUseCase(
    private val imageSprRepository: ImageSprRepository,
    private val externalFileDir: File?,
): UseCase<AddImageSprUseCase.Request, AddImageSprUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return imageSprRepository.addImage(request.kavlingKode, request.uri).map {
            Response(it)
        }
    }
}