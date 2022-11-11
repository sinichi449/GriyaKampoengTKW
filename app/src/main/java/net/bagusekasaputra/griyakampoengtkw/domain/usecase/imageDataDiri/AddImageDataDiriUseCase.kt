package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.util.ImageUtil
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddImageDataDiriUseCase @Inject constructor(
    private val imageDataDiriRepository: ImageDataDiriRepository,
    private val externalFileDir: File?,
): UseCase<AddImageDataDiriUseCase.Request, AddImageDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        val dstUri = ImageUtil.copyImageAndGetUri(externalFileDir, request.uri, "img_${request.kavlingKode}")

        ImageUtil.deleteImagePickerLeftOver(externalFileDir)

        return imageDataDiriRepository.addImage(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }
}