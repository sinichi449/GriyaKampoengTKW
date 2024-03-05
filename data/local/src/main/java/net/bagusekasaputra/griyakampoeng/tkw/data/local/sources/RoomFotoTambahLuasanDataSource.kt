package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahLuasanModel
import java.io.File
import java.io.FileNotFoundException

class RoomFotoTambahLuasanDataSource(
    private val externalFilesDir: File?
): LocalFotoTambahLuasanDataSource {

    override suspend fun get(kavling: String, id: String): Result<FotoTambahLuasanModel?> {
        return try {
            val dstFile = File(externalFilesDir, FotoTambahLuasanModel.DST_FOLDER).let { root ->
                File(root, "$kavling/$id.png")
            }
            if (dstFile.exists()) {
                val model = FotoTambahLuasanModel(
                    tambahLuasanId = id,
                    kavling = kavling,
                    uri = dstFile.toUri().toString(),
                )

                Log.d("DEBUG_ME", "Getting file : ${dstFile.toUri()}")

                Result.success(model)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi error!"))
        }
    }

    override suspend fun insert(model: FotoTambahLuasanModel): Result<Nothing?> {
        return try {
            model.uri.toUri().toFile().let { srcFile ->
                File(externalFilesDir, "${FotoTambahLuasanModel.DST_FOLDER}/${model.getKavlingAndFilePath()}").let { dstFile ->
                    srcFile.copyTo(dstFile, true)

                    dstFile.toUri()
                }
            }

            ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            val dstFile = File(externalFilesDir, FotoTambahLuasanModel.DST_FOLDER)

            if (dstFile.deleteRecursively()) Result.success(null)
            else Result.failure(Exception("Terjadi kesalahan!"))
        } catch (e: Exception) {
            Result.success(null)
        }
    }

}