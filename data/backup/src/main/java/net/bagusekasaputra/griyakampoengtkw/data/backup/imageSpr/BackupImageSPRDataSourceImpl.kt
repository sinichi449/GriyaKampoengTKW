package net.bagusekasaputra.griyakampoengtkw.data.backup.imageSpr

import android.content.SharedPreferences
import android.util.Log
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoengtkw.data.backup.PATH_IMAGE_SPR
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageSPRDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import java.io.File

class BackupImageSPRDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupImageSPRDataSource {

    override fun getImageSPR(kavlingKode: String): Result<ImageSprModel?> {
        return try {
            val fileImageSpr = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/${PATH_IMAGE_SPR(kavlingKode)}")

            Log.d("DEBUG_ME", "BackupImageSPRDataSource: Checking SPR Kav. $kavlingKode in ${fileImageSpr.absolutePath} ...")

            if (fileImageSpr.exists()) {
                Log.d("DEBUG_ME", "BackupImageSPRDataSource: SPR $kavlingKode deserialized!")

                Result.success(
                    ImageSprModel(
                        kavlingKode = kavlingKode,
                        dstUri = fileImageSpr.toUri().toString(),
                    )
                )
            } else {
                Log.d("DEBUG_ME", "BackupImageSPRDataSource: NOT FOUND for Kav. $kavlingKode")

                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun createBackup(
        backupPath: String,
        listSprImage: List<ImageSprModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}