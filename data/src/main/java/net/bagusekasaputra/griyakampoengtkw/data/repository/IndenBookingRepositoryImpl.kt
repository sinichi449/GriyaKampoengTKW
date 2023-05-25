package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.net.Uri
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class IndenBookingRepositoryImpl(
    private val localDataSource: LocalIndenBookingDataSource,
    private val remoteDataSource: RemoteIndenBookingDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): IndenBookingRepository {

    private val localCacheTable = { keyId: String -> "indenBooking_${keyId}"}
    private val remoteCacheTable = { keyId: String -> "indenBooking/${keyId}"}

    override suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?> {
        return remoteDataSource.getAllKeyIds()
    }

    override suspend fun getDataDiri(keyId: String, dataMode: DataMode): Result<DataDiri?> {
//        val invalidCache = checkAndInvalidateCache(keyId)
//        val localModel = localDataSource.getDataDiri(keyId).getOrThrow()
//
//        // Fetch from remote data source if either the cache was invalid
//        // or the local data source returning null (probably after invalidate() call)
//        if (invalidCache || localModel == null) {
//            Log.d("INDEN_BOOKING", "Data Diri on Cache was invalid or Local Data Source is null! " +
//                    "Fetching from Remote Data Source now.")
//
//            remoteDataSource.getDataDiri(keyId).getOrThrow()?.also {
//                localDataSource.insertDataDiri(keyId, it)
//            }
//        } else {
//            Log.d("INDEN_BOOKING", "Data Diri on Local Data Source is okay, returning from it.")
//        }
//
//        val refreshedLocalResult = localDataSource.getDataDiri(keyId)
//        return DataUtil.mapSingleResult(
//            originResult = refreshedLocalResult,
//            targetMapper = MyObjectMapper::mapDataDiri,
//        )
        val remoteResult = remoteDataSource.getDataDiri(keyId)

        return DataUtil.mapSingleResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapDataDiri,
        )
    }

    override suspend fun getAllPembayaran(
        keyId: String,
        dataMode: DataMode
    ): Result<List<Pembayaran>?> {
        val remoteResult = remoteDataSource.getAllPembayaran(keyId)

        return DataUtil.mapListResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapPembayaran,
        )
    }

    override suspend fun getHargaRumah(
        keyId: String,
        dataMode: DataMode
    ): Result<HargaRumahIndenBooking?> {
        val remoteResult = remoteDataSource.getHargaRumah(keyId)

        return DataUtil.mapSingleResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapHargaRumah,
        )
    }

    override suspend fun getFotoIdentitas(keyId: String, dataMode: DataMode): Result<Uri?> {
        val isInvalidCache = checkAndInvalidateCache(keyId)
        val localModel = localDataSource.getFotoIdentitas(keyId).getOrThrow()

        if (isInvalidCache || localModel == null) {
            Log.d("INDEN_BOOKING", "Data Diri on Local Data Source either invalidated or null!" +
                    " Fetching from Remote Data Source now.")

            val remoteModel = remoteDataSource.getFotoIdentitas(keyId).getOrThrow()
            remoteModel?.also {
                localDataSource.insertFotoIdentitas(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Data Diri returning from Local Data Source!")
        }

        return localDataSource.getFotoIdentitas(keyId)
    }

    private suspend fun checkAndInvalidateCache(keyId: String): Boolean {
        var isInvalid = false

        val localTimestamp = localMetadata.get(localCacheTable(keyId))?.timestamp
        val remoteTimestamp = remoteMetadata.get(remoteCacheTable(keyId))?.timestamp
        if (localTimestamp != remoteTimestamp) {
            isInvalid = true
            localDataSource.invalidate(keyId).getOrThrow()

            remoteTimestamp?.also {
                localMetadata.insert(MetadataModel(localCacheTable(keyId), it))
            }
        }

        return isInvalid
    }
}