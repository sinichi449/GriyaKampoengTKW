package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import java.io.File

class AddImageDataDiriUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository,
    private val externalFileDir: File?,
): UseCase<AddImageDataDiriUseCase.Request, AddImageDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        // First copy file to our apps storage on Android/data/<package>/Pictures
        val dstUri = ImageUtil.copyImageAndGetUri(externalFileDir, request.uri, "img_${request.kavlingKode}")

        // Delete the leftovers from ImagePicker library
        ImageUtil.deleteImagePickerLeftOver(externalFileDir)

        return imageDataDiriRepository.addImage(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }
}