package net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.util.ImageUtil
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddFotoKuitansiUseCase @Inject constructor(
    private val fotoKuitansiRepository: FotoKuitansiRepository,
    private val externalFileDir: File?,
): UseCase<AddFotoKuitansiUseCase.Request, AddFotoKuitansiUseCase.Response>() {

    data class Request(val kavlingKode: String, val srcUri: Uri): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        val dstUri = ImageUtil.copyImageAndGetUri(
            externalFileDir,
            request.srcUri,
            "kuitansi_${request.kavlingKode}"
        )

        ImageUtil.deleteImagePickerLeftOver(externalFileDir)

        return fotoKuitansiRepository.addFoto(request.kavlingKode, dstUri).map {
            Response(it)
        }
    }
}