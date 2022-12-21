package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ImageUtil
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import java.io.File

class LocalImageDataDiriDataSourceImpl(
    myRoomDatabase: MyRoomDatabase,
    private val externalFilesDir: File?,
): LocalImageDataDiriDataSource {

    private val imageDao = myRoomDatabase.getImageDataDiriDao()
    private val imageFile = File(externalFilesDir, "data_diri_images")

    override fun getByKavlingKode(kavlingKode: String): Flow<ImageDataDiriModel?> {
        return flow {
            try {
                val imgDataDiri = imageDao.getByKavlingKode(kavlingKode)?.let {
                    ImageDataDiriModel(
                        kavlingKode = it.kavlingKode,
                        imgUri = it.imgUri
                    )
                }

                emit(imgDataDiri)
            } catch (e: Exception) {
                e.printStackTrace()

                throw e
            }
        }
    }

    override suspend fun insert(
        imageDataDiriModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            // First copy file to our apps storage on Android/data/<package>/Pictures
            val dstUri = ImageUtil.copyImageAndGetUri(externalFilesDir,
                Uri.parse(imageDataDiriModel.imgUri),
                ImageDataDiriModel.DST_FOLDER,
                imageDataDiriModel.getFilename())

            // Delete the leftovers from ImagePicker library
            ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

            val imageDataDiri = imageDataDiriModel.let {
                ImageDataDiriRoomEntity(
                    kavlingKode = it.kavlingKode,
                    imgUri = dstUri.toString(),
                )
            }

            imageDao.insert(imageDataDiri)
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.cause)
        }
    }

    override suspend fun delete(
        imageDataDiriModel: ImageDataDiriModel
    ): Result<Nothing?> {
        try {
            val imageDataDiri = imageDataDiriModel.let {
                ImageDataDiriRoomEntity(
                    kavlingKode = it.kavlingKode,
                    imgUri = it.imgUri,
                )
            }

            imageDao.delete(imageDataDiri = imageDataDiri)

            return Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            return Result.failure(e)
        }
    }

    override suspend fun deleteByKavlingKode(
        kavlingKode: String,
    ): Result<Nothing?> {
        return try {
            // Also delete the file
            imageDao.getByKavlingKode(kavlingKode)?.let {
                File(externalFilesDir, "${ImageDataDiriModel.DST_FOLDER}/${kavlingKode}_data_diri.png")
                    .delete()
            }

            imageDao.deleteByKavlingKode(kavlingKode)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun update(
        oldModel: ImageDataDiriModel,
        newModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getUriByKavlingKode(
        kavlingKode: String,
        onSuccess: (uri: Uri) -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        try {
            imageDao.deleteAll()
            imageFile.listFiles()?.forEach {
                it.delete()
            }
        } catch (e: Exception) {
            throw e
        }
    }
}