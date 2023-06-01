package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriIndenBookingRepository
import java.io.File

class ImageDataDiriIndenBookingRepositoryImpl(
    private val localDataSource: LocalImageDataDiriIndenBookingDataSource,
    private val remoteDataSource: RemoteImageDataDiriIndenBookingDataSource,
    private val externalFileDir: File?,
    private val cacheHelper: CacheHelper,
): ImageDataDiriIndenBookingRepository {

    private val cacheLocalTable = "imageDataDiriIndenBooking"
    private val cacheRemoteTable = "indenBooking/imageDataDiri"

    override suspend fun get(keyId: String): Result<ImageDataDiriIndenBooking?> {
        return try {
            val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                cacheLocalTable, cacheRemoteTable,
                onInvalid = {
                    localDataSource.deleteAll()
                }
            )
            val localModel = localDataSource.get(keyId).getOrThrow()

            val imageDataDiri: ImageDataDiriIndenBooking?
            if (isInvalidCache || (localModel == null)) {
                Log.d("INDEN_BOOKING", "Image Data Diri on Cache was invalid or Local Data Source is null! ($keyId) " +
                        "Fetching from Remote Data Source now.")

                val savePath = ImageDataDiriIndenBooking(keyId, "")
                    .getUriSavePath(externalFileDir)
                val model = ImageDataDiriIndenBookingModel(keyId, savePath.toString())
                val remoteAvailable = remoteDataSource.download(model).getOrThrow()

                imageDataDiri = if (remoteAvailable) {
                    Log.d("INDEN_BOOKING", "Image Data Diri is available on Remote and successfully downloaded!")

                    localDataSource.insert(model).getOrThrow()

                    val refreshedLocalModel = localDataSource.get(keyId).getOrThrow()
                    MyObjectMapper.mapImageDataDiriIndenBooking(refreshedLocalModel)
                } else {
                    Log.d("INDEN_BOOKING", "Image Data Diri is NOT Available on Remote!")

                    null
                }

            } else {
                Log.d("INDEN_BOOKING", "Image Data Diri on Local Data Source is okay, returning from it.")

                imageDataDiri = MyObjectMapper.mapImageDataDiriIndenBooking(localModel)
            }

            Result.success(imageDataDiri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isExist(keyId: String): Result<Boolean> {
        return remoteDataSource.isExist(keyId)
    }
}