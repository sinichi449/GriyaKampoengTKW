package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import java.io.File

class LocalImageDataDiriDataSourceImpl(
    myRoomDatabase: MyRoomDatabase,
    externalFilesDir: File?
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