package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddImageSprUseCase @Inject constructor(
    private val imageSprRepository: ImageSprRepository,
    private val externalFileDir: File?,
): UseCase<AddImageSprUseCase.Request, AddImageSprUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        // First copy SPR image to the apps storage in Android/data/<package>/Pictures
        val dstUri = ImageUtil.copyImageAndGetUri(externalFileDir, request.uri, "spr_${request.kavlingKode}")

        // Deleting the ImagePicker library leftovers
        ImageUtil.deleteImagePickerLeftOver(externalFileDir)

        return imageSprRepository.addImage(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }
}