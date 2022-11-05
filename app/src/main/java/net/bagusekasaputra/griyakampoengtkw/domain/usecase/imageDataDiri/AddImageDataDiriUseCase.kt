package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import android.net.Uri
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
    private val pictureDirectory: File?,
): UseCase<AddImageDataDiriUseCase.Request, AddImageDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String, val uri: Uri): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        val dstUri = copyFileAndGetUri(request.uri, "img_${request.kavlingKode}")
        return imageDataDiriRepository.addImage(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }

    private fun copyFileAndGetUri(srcUri: Uri, fileName: String): Uri  {
        val src = srcUri.toFile()
        val dst = File(pictureDirectory, fileName)

        src.copyTo(dst, true)

        return dst.toUri()
    }
}