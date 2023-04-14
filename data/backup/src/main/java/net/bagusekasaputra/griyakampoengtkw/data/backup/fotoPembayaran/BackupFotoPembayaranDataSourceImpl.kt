package net.bagusekasaputra.griyakampoengtkw.data.backup.fotoPembayaran

import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoengtkw.data.backup.FOLDER_FOTO_PEMBAYARAN
import net.bagusekasaputra.griyakampoengtkw.data.backup.PATH_FOTO_PEMBAYARAN
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import java.io.File

class BackupFotoPembayaranDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupFotoPembayaranDataSource {

    private fun getFileFotoPembayaran(kavlingKode: String, termin: String): File {
        return File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/${PATH_FOTO_PEMBAYARAN(kavlingKode, termin)}")
    }

    override suspend fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Result<FotoPembayaranModel?> {
        return try {
            val fileFotoPembayaran = getFileFotoPembayaran(kavlingKode, termin)

            Log.d("DEBUG_ME", "BackupFotoPembayaranDataSource: Mengecek folder ${fileFotoPembayaran.absolutePath} ...")

            if (fileFotoPembayaran.exists()) {
                Log.d("DEBUG_ME", "BackupFotoPembayaranDataSource: Foto Pembayaran $kavlingKode $termin deserialized!")

                Result.success(
                    FotoPembayaranModel(
                        id = null,
                        kavlingKode = kavlingKode,
                        termin = termin,
                        uriStr = fileFotoPembayaran.toUri().toString(),
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun isFotoPembayaranExist(kavlingKode: String, termin: String): Result<Boolean?> {
        return try {
            val fileFotoPembayaran = getFileFotoPembayaran(kavlingKode, termin)

            Log.d("DEBUG_ME", "BackupFotoPembayaranDataSource: Mode isExist ${fileFotoPembayaran.absolutePath} ...")

            if (fileFotoPembayaran.exists()) {
                Log.d("DEBUG_ME", "BackupFotoPembayaranDataSource: Foto Pembayaran $kavlingKode $termin tersedia!")

                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listFotoPembayaran: List<FotoPembayaranModel>
    ): Result<Nothing?> {
        return try {
            if (listFotoPembayaran.isEmpty()) {
                Log.d("DEBUG_ME", "BackupFotoPembayaran: Argument \"listFotoPembayaran\" is EMPTY!")
            }

            val backupFolder = File("$backupPath/$FOLDER_FOTO_PEMBAYARAN")
            if (!backupFolder.exists()) backupFolder.mkdir()

            listFotoPembayaran.forEach {
                val fotoPembayaranFile = it.uriStr.toUri().toFile()
                var targetFolder = File(backupFolder, it.kavlingKode)

                if (!targetFolder.exists()) targetFolder.mkdir()

                targetFolder = File(targetFolder, it.getFilename())

                fotoPembayaranFile.copyTo(targetFolder, true)
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}