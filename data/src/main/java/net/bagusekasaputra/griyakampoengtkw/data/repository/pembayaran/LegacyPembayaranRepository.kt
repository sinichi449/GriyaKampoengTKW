package net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapPembayaran
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

@Deprecated("Migrated to DefaultPembayaranRepository")
class LegacyPembayaranRepository(
    private val localPembayaranDataSource: LocalPembayaranDataSource,
    private val remotePembayaranDataSource: RemotePembayaranDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val cacheHelper: CacheHelper,
): PembayaranRepository {

    private val metadataTable = "formPembayaran"
    private val pembayaranIndenBookingLocalTable = "pembayaranIndenBooking"
    private val pembayaranIndenBookingRemoteTable = "indenBooking/pembayaran"

    override suspend fun getByKavlingAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<Pembayaran?> {
        return try {
            val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                metadataTable, metadataTable,
                onInvalid = {
                    localPembayaranDataSource.deleteAll()
                }
            )
            val localModel = localPembayaranDataSource.getByKavlingAndTermin(kavlingKode, termin).getOrThrow()

            val pembayaran = if (isInvalidCache || localModel == null) {
                val remoteModel = remotePembayaranDataSource.getByKavlingAndTermin(kavlingKode, termin).getOrThrow()

                if (remoteModel != null) {
                    localPembayaranDataSource.addPembayaranModel(kavlingKode, remoteModel).getOrThrow()

                    val refreshedLocalModel = localPembayaranDataSource.getByKavlingAndTermin(kavlingKode, termin).getOrThrow()

                    MyObjectMapper.mapPembayaran(refreshedLocalModel!!)
                } else {
                    null
                }
            } else {
                MyObjectMapper.mapPembayaran(localModel)
            }

            Result.success(pembayaran)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            checkCache()

            val batchPembayaran = mutableMapOf<String, List<Pembayaran>?>()

            // Consolidating models
            listKavling.forEach { kavling ->
                val localModel = localPembayaranDataSource.getAllPembayaran(kavling)
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling Local : ${it.message}")
                    }
                    .getOrNull()

                if (localModel.isNullOrEmpty()) {
                    remotePembayaranDataSource.getAllPembayaran(kavling)
                        .onSuccess { listPembayaran ->
                            listPembayaran?.forEach {
                                localPembayaranDataSource.addPembayaranModel(
                                    kavlingKode = kavling,
                                    pembayaranModel = it,
                                ).onFailure {
                                    Log.d("DEBUG_ME", "PembayaranRepoImpl:53 onFailure -> ${it.message}")
                                }
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling Remote : ${it.message}")
                        }

                    // Second try
                    localPembayaranDataSource.getAllPembayaran(kavling)
                        .onSuccess { listPembayaran ->
                            batchPembayaran[kavling] = listPembayaran?.map { mapPembayaran(it) }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling on PembaranRepoImpl:63 : ${it.message}")
                        }
                } else {
                    batchPembayaran[kavling] = localModel.map { mapPembayaran(it) }
                }
            }

            emit(Result.success(batchPembayaran))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override fun fromBackupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            val mapPembayaran = mutableMapOf<String, List<Pembayaran>?>()

            listKavling.forEach { kavling ->
                val remoteResult = remotePembayaranDataSource.getAllFromBackup(backupName, kavling)
                if (remoteResult.isSuccess) {
                    val listModel = remoteResult.getOrNull()
                    val listPembayaran = listModel?.map { mapPembayaran(it) }

                    mapPembayaran[kavling] = listPembayaran
                } else {
                    val errorCause = remoteResult.exceptionOrNull() ?: Throwable("Unknown Error getting Backup \"$backupName\" List Pembayaran at kavling $kavling")

                    errorCause.printStackTrace()

                    emit(Result.failure(errorCause))
                }
            }

            emit(Result.success(mapPembayaran))
        }
    }

    override fun getAllPembayaran(
        kavlingKode: String,
        dataMode: DataMode,
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            // TODO: Check metadata

            val flowOffline = flow {
                val localResult = localPembayaranDataSource.getAllPembayaran(kavlingKode)
                val mapResult = DataUtil.mapListResult(
                    originResult = localResult,
                    targetMapper = ::mapPembayaran,
                )

                emit(mapResult)
            }
            val flowOnline = flow {
                // First, we request to the remote
                val remoteResult = remotePembayaranDataSource.getAllPembayaran(kavlingKode)

                if (remoteResult.isSuccess) {
                    // If success, then we write to the local data
                    remoteResult.getOrNull()?.forEach {
                        localPembayaranDataSource.addPembayaranModel(kavlingKode, it)
                    }

                    // Then emit the result
                    val mapResult = DataUtil.mapListResult(
                        originResult = remoteResult,
                        targetMapper = ::mapPembayaran,
                    )
                    emit(mapResult)
                } else {
                    // If remote request is failed, we emit the error
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("Terjadi kesalahan tak diketahui pada server saat mendapatkan data Pembayaran!")
                    emit(Result.failure(errorCause))

                    // Then get from local instead
                    emitAll(flowOffline)
                }
            }

            when (dataMode) {
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.DATA_LAMA -> TODO("Not yet implemented")
            }
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        return flow {
            // First, we request to the remote
            val remoteResult = remotePembayaranDataSource.getAllPembayaran(kavlingKode)

            val mapResult = DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = ::mapPembayaran,
            )
            emit(mapResult)
        }
    }

    override suspend fun refreshCache(kavlings: List<String>): Result<Nothing?> {
        return try {
            localPembayaranDataSource.deleteAll().getOrThrow()

            kavlings.forEach { kavlingKode ->
                val remoteResult = remotePembayaranDataSource.getAllPembayaran(kavlingKode).getOrThrow()

                remoteResult?.also { pembayaranModels ->
                    if (pembayaranModels.isNotEmpty()) {
                        localPembayaranDataSource.addAllPembayaranModel(kavlingKode, pembayaranModels)
                    } else {
                        Log.d("INIT_CACHE", "Pembayaran can't be refreshed because Remote Data Source returning an empty list!")
                    }
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun addPembayaran(
        kavlingKode: String,
        pembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            val remoteResult = remotePembayaranDataSource.addPembayaranModel(
                kavlingKode,
                pembayaranModel = mapPembayaran(pembayaran)
            )
            Log.d("DEBUG_ME", "Inserting pembayaran blblbl")

            if (remoteResult.isSuccess) {
                emit(Result.success(true))
            } else {
                val errorCause = remoteResult.exceptionOrNull()
                    ?: Throwable("Terjadi kesalahan tak diketahui saat menambah data Pembayaran!")
                emit(Result.failure(errorCause))
            }
        }
    }

    override suspend fun updatePembayaran(
        kavlingKode: String,
        newPembayaran: Pembayaran
    ): Result<Nothing?> {
        return try {
            val termin = newPembayaran.termin
            val newModel = MyObjectMapper.mapPembayaran(newPembayaran)

            // Remote Update
            remotePembayaranDataSource.update(kavlingKode, termin, newModel).getOrThrow()

            // Cache Update
            cacheHelper.updateMetadata(metadataTable, metadataTable).getOrThrow()

            // Local Update
            localPembayaranDataSource.update(kavlingKode, termin, newModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
    ): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            // delete both from local and remote
            val localResult = localPembayaranDataSource.deletePembayaranModelByTermin(kavlingKode, termin)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranDataSource.deletePembayaranModelByTermin(kavlingKode, termin)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    /**
     * Inden Booking related
     */
    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<Pembayaran>?> {
        val invalidCache = cacheHelper.checkAndInvalidateCache(
            pembayaranIndenBookingLocalTable,
            pembayaranIndenBookingRemoteTable,
            onInvalid = {
                localPembayaranDataSource.deleteAllFromIndenBooking()
            }
        )
        val localModel = localPembayaranDataSource.getAllFromIndenBooking(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (invalidCache || localModel.isNullOrEmpty()) {
            Log.d("INDEN_BOOKING", "Pembayaran on Cache was invalid or Local Data Source is null! ($keyId) " +
                    "Fetching from Remote Data Source now.")

            remotePembayaranDataSource.getAllFromIndenBooking(keyId).getOrThrow()?.also {
                it.forEach { pembayaranModel ->
                    Log.d("INTERNAL_INDEN_BOOKING", "Begin insertion for ${pembayaranModel.termin} !")
                }
                localPembayaranDataSource.insertAllFromIndenBooking(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Pembayaran on Local Data Source is okay, returning from it.")
        }

        val refreshedLocalResult = localPembayaranDataSource.getAllFromIndenBooking(keyId)
        return DataUtil.mapListResult(
            originResult = refreshedLocalResult,
            targetMapper = MyObjectMapper::mapPembayaran,
        )
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        pembayaran: Pembayaran
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val model = MyObjectMapper.mapPembayaran(pembayaran)

            // Remote Insertion
            remotePembayaranDataSource.insertFromIndenBooking(keyId, model)
                .onSuccess {
                    // Update Cache
                    cacheHelper.updateMetadata(
                        pembayaranIndenBookingLocalTable, pembayaranIndenBookingRemoteTable
                    )
                        .onSuccess {
                            // Local Insertion
                            localPembayaranDataSource.insertFromIndenBooking(keyId, model)
                                .onSuccess {
                                    trySendBlocking(Result.success(null))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Pembayaran cache is invalid! Purging local data now.")
            localPembayaranDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Pembayaran Local : ${it.message}")
                }
            localMetadata.insert(MetadataModel(metadataTable, remoteTimestamp))
        }
    }

    private suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable) ?: MetadataModel(metadataTable, 0L)
        val newMetadata = MetadataModel(metadataTable, currentTimemillis)

        localMetadata.insert(newMetadata)
        remoteMetadata.update(oldMetadata, newMetadata)
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            // delete both from local and remote
            val localResult = localPembayaranDataSource.deleteAllPembayaranModel(kavlingKode)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranDataSource.deleteAllPembayaranModel(kavlingKode)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

}