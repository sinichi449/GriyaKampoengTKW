package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ImageUtil
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.ImageDataDiriRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import java.io.File

class LocalImageDataDiriDataSourceImpl(
    myRoomDatabase: MyRoomDatabase,
    private val externalFilesDir: File?,
    private val imageIndenBooking: LocalFotoIdentitasDataSource,
): LocalImageDataDiriDataSource {

    private val imageDao = myRoomDatabase.getImageDataDiriDao()
    private val imageFile = File(externalFilesDir, ImageDataDiriModel.DST_FOLDER)

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
        fromRemote: Boolean,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    ) {
        try {
            val imageDataDiri: ImageDataDiriRoomEntity
            if (fromRemote.not()) {
                // First copy file to our apps storage on Android/data/<package_name>/files/data_diri_images
                val dstUri = ImageUtil.copyImageAndGetUri(externalFilesDir,
                    Uri.parse(imageDataDiriModel.imgUri),
                    ImageDataDiriModel.DST_FOLDER,
                    imageDataDiriModel.getFilename())

                // Delete the leftovers from ImagePicker library
                ImageUtil.deleteImagePickerLeftOver(externalFilesDir)

                imageDataDiri = imageDataDiriModel.let {
                    ImageDataDiriRoomEntity(
                        kavlingKode = it.kavlingKode,
                        imgUri = dstUri.toString(),
                    )
                }
            } else {
                imageDataDiri = imageDataDiriModel.let {
                    ImageDataDiriRoomEntity(
                        kavlingKode = it.kavlingKode,
                        imgUri = it.imgUri
                    )
                }
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
            e.printStackTrace()

            throw e
        }
    }


    /**
     * Inden Booking related
     */
    override suspend fun getFromIndenBooking(keyId: String): Result<Uri?> {
        return imageIndenBooking.get(keyId)
    }

    override suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?> {
        return imageIndenBooking.insert(keyId, uri)
    }

    override suspend fun updateFromIndenBooking(keyId: String, newUri: Uri): Result<Nothing?> {
        return imageIndenBooking.update(keyId, newUri)
    }

    override suspend fun deleteFromIndenBooking(keyId: String): Result<Nothing?> {
        return imageIndenBooking.delete(keyId)
    }

    override suspend fun deleteAllFromIndenBooking(): Result<Nothing?> {
        return imageIndenBooking.deleteAll()
    }
}

/**
 * This interface will prevent dependency to Inden Booking counterpart, since it is very unstable,
 * and instead inverting that relation.
 *
 * (Dependency Inversion?)
 */
interface LocalFotoIdentitasDataSource {

    suspend fun get(keyId: String): Result<Uri?>

    suspend fun insert(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun update(keyId: String, newUri: Uri): Result<Nothing?>

    suspend fun delete(keyId: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}