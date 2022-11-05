package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.LocalImageDataDiriSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDataDiriRoomSource @Inject constructor(
    private val imageDao: ImageDataDiriDao
): LocalImageDataDiriSource {

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
                ImageDataDiriRoom(
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
        TODO("Not yet implemented")
    }

    override suspend fun update(
        oldModel: ImageDataDiriModel,
        newModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        TODO("Not yet implemented")
    }
}