package net.bagusekasaputra.griyakampoengtkw.data.backup.imageDataDiri

import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoengtkw.data.backup.PATH_IMAGE_DATA_DIRI
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import java.io.File

class BackupImageDataDiriDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
): BackupImageDataDiriDataSource {

    override suspend fun getImageDataDiri(kavlingKode: String): Result<ImageDataDiriModel?> {
        return try {
            val fileImageDataDiri = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/${PATH_IMAGE_DATA_DIRI(kavlingKode)}")

            if (fileImageDataDiri.exists()) {
                Log.d("DEBUG_ME", "BackupImageDataDiri: Found backup kavling $kavlingKode!")
                val uriImageDataDiri = fileImageDataDiri.toUri()

                Result.success(ImageDataDiriModel(
                    kavlingKode = kavlingKode,
                    imgUri = uriImageDataDiri.toString(),
                ))
            } else {
                Log.d("DEBUG_ME", "BackupImageDataDiri: NOT Found backup for kavling $kavlingKode")
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listImageDataDiri: List<ImageDataDiriModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}