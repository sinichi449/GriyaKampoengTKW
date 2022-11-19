package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri

import android.net.Uri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

class RoomLocalImageDataDiriDataRepository(
    myRoomDatabase: MyRoomDatabase,
): LocalImageDataDiriDataSource {

    private val imageDao = myRoomDatabase.getImageDataDiriDao()

    override suspend fun getByKavlingKode(
        kavlingKode: String,
        onSuccess: (imageDataDiriModel: ImageDataDiriModel) -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            val imgDataDiri = imageDao.getByKavlingKode(kavlingKode).let {
                ImageDataDiriModel(
                    kavlingKode = it.kavlingKode,
                    imgUri = it.imgUri
                )
            }

            onSuccess(imgDataDiri)
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.cause)
        }
    }

    override suspend fun insert(
        imageDataDiriModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            val imageDataDiri = imageDataDiriModel.let {
                ImageDataDiriRoomEntity(
                    kavlingKode = it.kavlingKode,
                    imgUri = it.imgUri,
                )
            }

            imageDao.insert(imageDataDiri)
            onSuccess()
        } catch (e: Exception) {
            onFailure(e.cause)
        }
    }

    override suspend fun delete(
        imageDataDiriModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            val imageDataDiri = imageDataDiriModel.let {
                ImageDataDiriRoomEntity(
                    kavlingKode = it.kavlingKode,
                    imgUri = it.imgUri,
                )
            }

            imageDao.delete(imageDataDiri = imageDataDiri)
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.cause)
        }
    }

    override suspend fun deleteByKavlingKode(
        kavlingKode: String,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            imageDao.deleteByKavlingKode(kavlingKode)
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.cause)
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
        try {
            val imageDataDiriRoom = imageDao.getByKavlingKode(kavlingKode)

            onSuccess(Uri.parse(imageDataDiriRoom.imgUri))
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.cause)
        }
    }
}