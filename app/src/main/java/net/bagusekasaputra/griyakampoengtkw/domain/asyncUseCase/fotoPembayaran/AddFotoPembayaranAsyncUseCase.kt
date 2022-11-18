package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddFotoPembayaranAsyncUseCase @Inject constructor(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
    private val externalFilesDir: File?,
): AsyncUseCase<AddFotoPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavlingKode: String, val termin: String, val uri: Uri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        val fotoPembayaran = FotoPembayaran(
            kavlingKode = request.kavlingKode,
            termin = request.termin,
            uri = request.uri,
        )
        // It turns out that I can't use ImageUtil because it is only copying file to Picture.
        // The Foto Pembayaran should be put in Picture/fotoPembayaran instead.
        val fileSrc = request.uri.toFile()
        val fileDest = fotoPembayaran.getFile(externalFilesDir)

        fileSrc.copyTo(fileDest, true)

        ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

        // OH! AND I FORGET TO REPLACE THE URI!!
        fotoPembayaran.uri = fileDest.toUri()

        return fotoPembayaranRepository.addFotoPembayaran(
            kavlingKode = request.kavlingKode,
            termin = request.termin,
            fotoPembayaran = fotoPembayaran,
        )
    }
}