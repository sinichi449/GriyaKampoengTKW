package net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.device

import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import java.io.File

class DeviceFotoPembayaranDataSource(
    private val externalFilesDir: File?,
): LocalFotoPembayaranDataSource {


    private val fotoPembayaranFolder = "fotoPembayaran"

    override suspend fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Result<FotoPembayaranModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun addFotoPembayaran(fotoPembayaranModel: FotoPembayaranModel): Result<Nothing?> {
        // It turns out that I can't use ImageUtil because it is only copying file to Picture directory.
        // The Foto Pembayaran should be put in Picture/fotoPembayaran instead.
        val fileSrc = fotoPembayaranModel.getUri().toFile()
        val fileDest = fotoPembayaranModel.getFile(externalFilesDir)

        fileSrc.copyTo(fileDest, true)

        ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

        return Result.success(null)
    }

    override suspend fun deleteById(id: Long): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByKavlingKodeAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        // Here we also delete the File physically.
        // First we create the dummy object in order to get access to getFile() method and
        // invoke delete().
        val fotoPembayaran = FotoPembayaranModel(
            kavlingKode = kavlingKode,
            termin = termin,
            uriStr = Uri.EMPTY.toString(),
        )

        // Now we can access getFile() method.
        val toBeDeleted = fotoPembayaran.getFile(externalFilesDir)
        toBeDeleted.delete()

        // I don't care whether the file is properly deleted or not. Because, even if it failed
        // to be deleted, that only means the Foto Pembayaran in question is either invalid
        // or missing, all the reason to delete the Uri of that file in the local database.
        return Result.success(null)
    }

    override suspend fun deleteAllFotoPembayaran(kavlingKode: String): Result<Nothing?> {
        val filePath = File(externalFilesDir, "${Environment.DIRECTORY_PICTURES}/${fotoPembayaranFolder}")

        val listMatchingFile = filePath.listFiles { _: File?, s: String? ->
            (s?.startsWith(kavlingKode) == true)
        }

        listMatchingFile?.forEach {
            it.delete()
        }

        return Result.success(null)
    }

    override suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getFotoUri(kavlingKode: String, termin: String): Result<Uri?> {
        val fotoPembayaranModel = FotoPembayaranModel(
            kavlingKode = kavlingKode,
            termin = termin,
            uriStr = "",
        )

        val filePath = fotoPembayaranModel.getFile(externalFilesDir)

        return Result.success(filePath.toUri())
    }
}