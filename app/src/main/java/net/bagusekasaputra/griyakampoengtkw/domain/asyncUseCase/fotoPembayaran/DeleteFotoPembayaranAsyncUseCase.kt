package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteFotoPembayaranAsyncUseCase @Inject constructor(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
    private val externalFilesDir: File?
): AsyncUseCase<DeleteFotoPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavlingKode: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        // Here we also delete the File physically
        // First we create the dummy object in order to get access to getFile() method
        val fotoPembayaran = FotoPembayaran(
            kavlingKode = request.kavlingKode,
            termin = request.termin,
            uri = Uri.EMPTY,
        )

        // Now we can access getFile() method.
        val toBeDeleted = fotoPembayaran.getFile(externalFilesDir)

        toBeDeleted.delete()

        // I don't care whether the file is properly deleted or not. Because, even if it failed
        // to be deleted, that only means the Foto Pembayaran in question is either invalid
        // or missing, all the reason to delete the Uri of that file in the local database.
        return fotoPembayaranRepository.deleteFotoPembayaran(request.kavlingKode, request.termin)
    }
}