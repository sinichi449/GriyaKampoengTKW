package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr

import android.net.Uri
import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ImageUtil
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import java.io.File

class LocalImageSprDataSourceImpl(
    private val roomDatabase: MyRoomDatabase,
    private val externalFilesDir: File?,
): LocalImageSprDataSource {

    private val imageSprDao = roomDatabase.getImageSprDao()
    private val imageFile = File(externalFilesDir, ImageSprModel.DST_ROOT)
    private val LOG_TAG = "DEBUG_ME"
    private val LOG_MSG = { func: String, msg: String ->
        "LocalImageSprImpl->$func(): $msg"
    }

    override suspend fun getByKavlingKode(
        kavlingKode: String
    ): Result<ImageSprModel?> {
        return try {
            val imgSpr = imageSprDao.getByKavlingKode(kavlingKode)?.let {
                ImageSprModel(
                    kavlingKode = it.kavlingKode,
                    dstUri = it.imgUri,
                )
            }

            roomDatabase.close()

            Result.success(imgSpr)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(LOG_TAG, LOG_MSG("getByKavlingKode", "Failed to get Image SPR: ${e.message}"))

            roomDatabase.close()

            Result.failure(e)
        }
    }

    override suspend fun insert(
        imageSprModel: ImageSprModel,
        fromRemote: Boolean,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    ) {
        try {
            val imgSpr: ImageSprRoomEntity
            if (fromRemote) {
                imgSpr = imageSprModel.let {
                    ImageSprRoomEntity(
                        kavlingKode = it.kavlingKode,
                        imgUri = it.dstUri,
                    )
                }
            } else {
                // First, copy file to our apps storage on Android/data/<package_name>/files/image_spr
                val dstUri = ImageUtil.copyImageAndGetUri(
                    externalFileDir = externalFilesDir,
                    srcUri = Uri.parse(imageSprModel.dstUri),
                    dstDir = ImageSprModel.DST_ROOT,
                    fileName = imageSprModel.getFilename(),
                )

                // Delete the leftovers from ImagePicker library
                ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

                imgSpr = imageSprModel.let {
                    ImageSprRoomEntity(
                        kavlingKode = it.kavlingKode,
                        imgUri = dstUri.toString(),
                    )
                }
            }

            imageSprDao.insert(imgSpr)

            roomDatabase.close()

            onSuccess()
        } catch (e: Exception) {
            roomDatabase.close()
            onFailure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            imageSprDao.deleteAll()
            roomDatabase.close()

            imageFile.listFiles()?.forEach {
                it.delete()
            }

            Result.success(null)

        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }


    private fun mapImageSprRoomEntity(imageSprRoomEntity: ImageSprRoomEntity): ImageSprModel {
        return imageSprRoomEntity.let {
            ImageSprModel(
                kavlingKode = it.kavlingKode,
                dstUri = it.imgUri,
            )
        }
    }

    private fun mapImageSprRoomEntity(imageSprModel: ImageSprModel): ImageSprRoomEntity {
        return imageSprModel.let {
            ImageSprRoomEntity(
                kavlingKode = it.kavlingKode,
                imgUri = it.dstUri,
            )
        }
    }
}