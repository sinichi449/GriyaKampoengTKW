package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
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
        val dstUri = copyImageAndGetUri(request.uri, "img_${request.kavlingKode}")

        deleteImagePickerLeftOver()

        return imageDataDiriRepository.addImage(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }

    private fun copyImageAndGetUri(srcUri: Uri, fileName: String): Uri  {
        val src = srcUri.toFile()
        val pictureDirectory = File(externalFileDir, Environment.DIRECTORY_PICTURES)
        val dst = File(pictureDirectory, fileName)

        src.copyTo(dst, true)

        return dst.toUri()
    }

    private fun deleteImagePickerLeftOver() {
        val imagePickerDirectory = File(externalFileDir, Environment.DIRECTORY_DCIM)
        imagePickerDirectory.listFiles()?.forEach { it?.delete() }

        imagePickerDirectory.delete()
    }
}