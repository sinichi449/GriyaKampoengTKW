package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaRumahIndenBookingRepository

class HargaRumahIndenBookingRepositoryImpl(
    private val localDataSource: LocalHargaRumahIndenBookingDataSource,
    private val remoteDataSource: RemoteHargaRumahIndenBookingDataSource,
    private val cacheHelper: CacheHelper,
): HargaRumahIndenBookingRepository {

    private val cacheLocalTable = "hargaRumahIndenBooking"
    private val cacheRemoteTable = "indenBooking/hargaRumah"

    override suspend fun get(
        keyId: String,
        dataMode: DataMode
    ): Result<HargaRumahIndenBooking?> {
        val invalidCache = cacheHelper.checkAndInvalidateCache(
            cacheLocalTable,
            cacheRemoteTable,
            onInvalid = {
                localDataSource.deleteAll()
            }
        )
        val localModel = localDataSource.get(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (invalidCache || localModel == null) {
            Log.d("INDEN_BOOKING", "Harga Rumah on Cache was invalid or Local Data Source is null! ($keyId) " +
                    "Fetching from Remote Data Source now.")

            remoteDataSource.get(keyId).getOrThrow()?.also {
                localDataSource.insert(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Harga Rumah on Local Data Source is okay, returning from it.")
        }

        val refreshedLocalResult = localDataSource.get(keyId)
        return DataUtil.mapSingleResult(
            originResult = refreshedLocalResult,
            targetMapper = MyObjectMapper::mapHargaRumah,
        )
    }
}