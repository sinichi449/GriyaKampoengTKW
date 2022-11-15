package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageSpr.room

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageSpr.LocalImageSprDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomImageSprDataSource @Inject constructor(
    roomDatabase: MyRoomDatabase,
): LocalImageSprDataSource {

    private val imageSprDao = roomDatabase.getImageSprDao()

    override suspend fun getByKavlingKode(
        kavlingKode: String,
        onSuccess: (imageSprModel: ImageSprModel) -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    ) {
        try {
            val sprEntity = imageSprDao.getByKavlingKode(kavlingKode)

            if (sprEntity != null) {
                onSuccess(mapImageSprRoomEntity(sprEntity))
            } else {
                onFailure(UnknownError("Foto SPR tidak ditemukan"))
            }

        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun insert(
        imageSprModel: ImageSprModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    ) {
        try {
            imageSprDao.insert(mapImageSprRoomEntity(imageSprModel))

            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
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