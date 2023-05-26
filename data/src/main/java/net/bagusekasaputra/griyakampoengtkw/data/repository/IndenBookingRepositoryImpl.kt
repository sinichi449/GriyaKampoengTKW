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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class IndenBookingRepositoryImpl(
    private val localDataSource: LocalIndenBookingDataSource,
    private val remoteDataSource: RemoteIndenBookingDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): IndenBookingRepository {

    private val fotoIdentitasLocalTable = "fotoIdentitasIndenBooking"
    private val fotoIdentitasRemoteTable = { keyId: String ->
        "indenBooking/${keyId}/dataDiri"
    }


    override suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?> {
        return remoteDataSource.getAllKeyIds()
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
        val isInvalidCache = checkAndInvalidateCache(
            fotoIdentitasLocalTable,
            fotoIdentitasRemoteTable(keyId),
            onInvalid = {
                localDataSource.invalidateFotoIdentitas()
            }
        )
        val localModel = localDataSource.getFotoIdentitas(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (isInvalidCache || localModel == null) {
            Log.d("INDEN_BOOKING", "Foto Identitas on Local Data Source either invalidated or null!" +
                    " Fetching from Remote Data Source now.")

            val remoteModel = remoteDataSource.getFotoIdentitas(keyId).getOrThrow()
            remoteModel?.also {
                localDataSource.insertFotoIdentitas(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Foto Identitas returning from Local Data Source!")
        }

        return localDataSource.getFotoIdentitas(keyId)
    }

    private suspend fun checkAndInvalidateCache(
        localTable: String,
        remoteTable: String,
        onInvalid: suspend () -> Unit,
    ): Boolean {
        var isInvalid = false

        val localTimestamp = localMetadata.get(localTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(remoteTable)?.timestamp
        if (localTimestamp != remoteTimestamp) {
            isInvalid = true

            onInvalid()

            remoteTimestamp?.also {
                localMetadata.insert(MetadataModel(localTable, it))
            }
        }

        return isInvalid
    }
}