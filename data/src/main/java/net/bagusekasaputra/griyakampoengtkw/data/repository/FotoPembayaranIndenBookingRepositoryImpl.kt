package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.indenBooking.FotoPembayaranIndenBookingRepository
import java.io.File

class FotoPembayaranIndenBookingRepositoryImpl(
    private val localDataSource: LocalFotoPembayaranIndenBookingDataSource,
    private val remoteDataSource: RemoteFotoPembayaranIndenBookingDataSource,
    private val externalStorageFile: File?,
    private val cacheHelper: CacheHelper,
): FotoPembayaranIndenBookingRepository {

    private val cacheLocalTable = "fotoPembayaranIndenBooking"
    private val cacheRemoteTable = "indenBooking/fotoPembayaran"

    override suspend fun get(keyId: String, termin: String): Result<FotoPembayaranIndenBooking?> {
        return callbackFlow<Result<FotoPembayaranIndenBooking?>> {
            try {
                val isCacheInvalid = cacheHelper.checkAndInvalidateCache(
                    cacheLocalTable, cacheRemoteTable,
                    onInvalid = {
                        localDataSource.deleteAll().getOrThrow()
                    }
                )
                val localModel = localDataSource.get(keyId, termin).getOrThrow()

                val fotoPembayaran: FotoPembayaranIndenBooking?
                if (isCacheInvalid || (localModel == null)) {
                    Log.d("INDEN_BOOKING", "Foto Pembayaran on Cache was invalid or Local Data Source is null! ($keyId) " +
                            "Fetching from Remote Data Source now.")

                    // Create model and download from remote
                    val uriSavePath = FotoPembayaranIndenBooking(keyId, termin, "")
                        .getUriSavePath(externalStorageFile)
                    val model = FotoPembayaranIndenBookingModel(
                        keyId, termin, uriSavePath.toString()
                    )
                    val availableOnRemote = remoteDataSource.download(model).getOrThrow()

                    // If it available on remote, then download it and insert to local data source
                    fotoPembayaran = if (availableOnRemote) {
                        Log.d("INDEN_BOOKING", "Foto Pembayaran is available on Remote and successfully downloaded!")

                        // Insert ot local data source
                        localDataSource.insert(model).getOrThrow()

                        // Retry from local data source after insertion
                        val refreshedLocalModel = localDataSource.get(keyId, termin).getOrThrow()
                        MyObjectMapper.mapFotoPembayaranIndenBooking(refreshedLocalModel)
                    } else {
                        Log.d("INDEN_BOOKING", "Foto Pembayaran is NOT Available on Remote!")

                        null
                    }
                } else {
                    Log.d("INDEN_BOOKING", "Foto Pembayaran on Local Data Source is okay, returning from it.")

                    fotoPembayaran = MyObjectMapper.mapFotoPembayaranIndenBooking(localModel)
                }

                trySendBlocking(Result.success(fotoPembayaran))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }.first()
    }

    override suspend fun insert(fotoPembayaran: FotoPembayaranIndenBooking): Result<Nothing?> {
        return try {
            // Move image to appropriate directory first
            fotoPembayaran.moveToExternalStorage(externalStorageFile)
            val model = MyObjectMapper.mapFotoPembayaranIndenBooking(fotoPembayaran)

            // Remote insertion
            remoteDataSource.insert(model).getOrThrow()

            // Update cache
            cacheHelper.updateMetadata(cacheLocalTable, cacheRemoteTable).getOrThrow()

            // Local insertion
            localDataSource.insert(model).getOrThrow()

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isExist(keyId: String, termin: String): Result<Boolean> {
        return remoteDataSource.isExist(keyId, termin)
    }
}