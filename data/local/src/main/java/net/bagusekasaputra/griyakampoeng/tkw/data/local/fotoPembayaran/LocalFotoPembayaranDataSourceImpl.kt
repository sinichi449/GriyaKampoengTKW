package net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ImageUtil
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import java.io.File

class LocalFotoPembayaranDataSourceImpl(
    private val roomDatabase: MyRoomDatabase,
    private val externalFilesDir: File?,
): LocalFotoPembayaranDataSource {

    private val fotoPembayaranDao = roomDatabase.getFotoPembayaranDao()

    override suspend fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Result<FotoPembayaranModel?> {
        return callbackFlow<Result<FotoPembayaranModel?>> {
            try {
                val imgFotoPembayaran = fotoPembayaranDao.getFotoPembayaran(kavlingKode, termin)?.let {
                    FotoPembayaranModel(
                        kavlingKode = it.kavlingKode,
                        termin = it.termin,
                        uriStr = it.uriStr,
                    )
                }

                trySendBlocking(Result.success(imgFotoPembayaran))
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }.first()
    }

    override suspend fun addFotoPembayaran(
        fotoPembayaranModel: FotoPembayaranModel,
        fromRemote: Boolean
    ): Result<Nothing?> {
        try {
            val fotoPembayaran: FotoPembayaranEntity
            // The difference between fromRemote=false and -true is the dstUri.
            if (fromRemote) {
                fotoPembayaran = fotoPembayaranModel.let {
                    FotoPembayaranEntity(
                        kavlingKode = it.kavlingKode,
                        termin = it.termin,
                        uriStr = it.uriStr,
                    )
                }
            } else {
                Log.d("DEBUG_ME", "LocalFotoPembayaran->insert(): Getting Foto Pembayaran from ${fotoPembayaranModel.getKavlingAndFilePath()} on fromRemote=false parameter ...")

                // First copy file to our apps storage on Android/data/<package_name>/files/foto_pembayaran_images/<kavling>/
                val dstUri = fotoPembayaranModel.getUri().toFile().let { srcFile ->
                    File(externalFilesDir, "${FotoPembayaranModel.DST_FOLDER}/${fotoPembayaranModel.getKavlingAndFilePath()}").let { dstFile ->
                        srcFile.copyTo(dstFile, true)

                        dstFile.toUri()
                    }
                }

                // Delete the leftovers from ImagePicker library
                ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

                fotoPembayaran = fotoPembayaranModel.let {
                    FotoPembayaranEntity(
                        kavlingKode = it.kavlingKode,
                        termin = it.termin,
                        uriStr = dstUri.toString(),
                    )
                }
            }

            fotoPembayaranDao.insert(fotoPembayaran)



            return Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



            return Result.failure(e)
        }
    }

    override suspend fun deleteById(id: Long): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByKavlingKodeAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        // Also delete the file
        return try {
            FotoPembayaranModel(kavlingKode = kavlingKode, termin = termin).let { model ->
                File(externalFilesDir, "${FotoPembayaranModel.DST_FOLDER}/${model.getKavlingAndFilePath()}")
                    .delete()
            }

            fotoPembayaranDao.deleteByKavlingKodeAndTermin(kavlingKode, termin)

            Log.d("DEBUG_ME", "LocalFotoPembayaran->delete(): Deleting foto pembayaran $kavlingKode on termin $termin from Local Data success!")

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Log.d("DEBUG_ME", "LcoalFotoPembayaran->delete(): Failed to delete Local Data foto pembayaran : ${e.message}")

            Result.failure(e)
        }
    }

    override suspend fun deleteAllFotoPembayaran(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            fotoPembayaranDao.deleteAllInKavling(kavlingKode)
        }
    }

    override suspend fun deleteAll(kavlingKode: String) {
        try {
            fotoPembayaranDao.deleteAllInKavling(kavlingKode)
            File(externalFilesDir, "${FotoPembayaranModel.DST_FOLDER}/${kavlingKode}")
                .listFiles()?.forEach {
                    it.delete()
                }
        } catch (e: Exception) {
            e.printStackTrace()

            throw e
        }
    }

    override suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getFotoUri(kavlingKode: String, termin: String): Result<Uri?> {
        TODO("Not yet implemented")
    }

    private fun mapFotoPembayaranEntity(fotoPembayaranEntity: FotoPembayaranEntity): FotoPembayaranModel {
        return fotoPembayaranEntity.let {
            FotoPembayaranModel(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = it.uriStr,
            )
        }
    }

    private fun mapFotoPembayaranEntity(fotoPembayaranModel: FotoPembayaranModel): FotoPembayaranEntity {
        return fotoPembayaranModel.let {
            FotoPembayaranEntity(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = it.uriStr,
            )
        }
    }
}