package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapDataDiri
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class DataDiriRepositoryImpl(
    private val localDataDiriDataSource: LocalDataDiriDataSource,
    private val remoteDataDiriDataSource: RemoteDataDiriDataSource,
    private val remoteKavlingDataSource: RemoteKavlingDataSource,
    private val backupDataDiriDataSource: BackupDataDiriDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): DataDiriRepository {

    private val metadataTable = "dataDiri"
    private val dataDiriIndenBookingLocalTable = "dataDiriIndenBooking"
    private val dataDiriIndenBookingRemoteTable = "indenBooking/dataDiri"

    override fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>> {
        return flow {
            checkCache()

            val batchDataDiri = mutableMapOf<String, DataDiri?>()

            listKavling.forEach { kavling ->
                val localModel = localDataDiriDataSource.getDataDiri(kavling)
                    .onFailure {
                        Log.d("DEBUG_ME", "DataDiriRepoImpl:37 onFailure -> ${it.message}")
                    }
                    .getOrNull()

                if (localModel == null) {
                    remoteDataDiriDataSource.getDataDiri(kavling)
                        .onSuccess { model ->
                            model?.let {
                                localDataDiriDataSource.addDataDiri(kavling, model)
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "DataDiriRepoImpl:47 onFailure -> ${it.message}")
                        }

                    // Second try
                    localDataDiriDataSource.getDataDiri(kavling)
                        .onSuccess {
                            batchDataDiri[kavling] = if (it != null) mapDataDiri(it) else null
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "DataDiriRepoImpl:58 onFailure -> ${it.message}")
                        }
                } else {
                    batchDataDiri[kavling] = mapDataDiri(localModel)
                }
            }

            emit(Result.success(batchDataDiri))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>> {
        return callbackFlow {
            try {
                val mapDataDiri = mutableMapOf<String, DataDiri?>()

                listKavling.forEach { kavling ->
                    mapDataDiri[kavling] = getDataDiri(kavling, DataMode.DATA_LAMA).first().getOrThrow()
                }

                trySendBlocking(Result.success(mapDataDiri))
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }

    override fun getBatchFromRemoteBackup(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, DataDiri?>?>> {
        return flow {
            val mapDataDiri = mutableMapOf<String, DataDiri?>()

            listKavling.forEach { kavling ->
                val remoteResult = remoteDataDiriDataSource.getFromBackup(backupName, kavling)
                if (remoteResult.isSuccess) {
                    val model = remoteResult.getOrNull()
                    val dataDiri = model?.let { mapDataDiri(it) }

                    mapDataDiri[kavling] = dataDiri
                } else {
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("Unknown Error getting Backup \"$backupName\" Data Diri at kavling $kavling")
                    errorCause.printStackTrace()

                    emit(Result.failure(errorCause))
                }
            }

            emit(Result.success(mapDataDiri))
        }
    }

    override fun getDataDiri(kavlingKode: String, dataMode: DataMode): Flow<Result<DataDiri?>> {
        return flow {
            val flowOffline = flow {
                val getDataDiriFromLocal = localDataDiriDataSource.getDataDiri(kavlingKode)

                if (getDataDiriFromLocal.isSuccess)
                    emit(DataUtil.mapSingleResult(getDataDiriFromLocal, ::mapDataDiri))
                else
                    emit(Result.failure(Throwable("Error tak diketahui")))
            }
            val flowOnline = flow<Result<DataDiri?>> {
                // Get from remote
                val getDataDiriRemote = remoteDataDiriDataSource.getDataDiri(kavlingKode)

                if (getDataDiriRemote.isSuccess) {
                    // Emit the data diri
                    emit(DataUtil.mapSingleResult(getDataDiriRemote, ::mapDataDiri))

                    // Then save to local
                    getDataDiriRemote.getOrNull()?.let {
                        localDataDiriDataSource.addDataDiri(kavlingKode, it)
                    }
                } else {
                    // Emit the error
                    getDataDiriRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }

                    // Emit from local instead
                    emitAll(flowOffline)
                }
            }
            val flowDataLama = flow<Result<DataDiri?>> {
                backupDataDiriDataSource.getDataDiri(kavlingKode)
                    .onSuccess {
                        emit(DataUtil.mapSingleResult(
                            originResult = Result.success(it),
                            targetMapper = ::mapDataDiri,
                        ))
                    }
                    .onFailure {
                        emit(Result.failure(it))
                    }
            }

            when (dataMode) {
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.DATA_LAMA -> emitAll(flowDataLama)
            }
        }
    }

    override fun getFromRemoteBackup(backupName: String, kavlingKode: String): Flow<Result<DataDiri?>> {
        return flow {
            val remoteResult = remoteDataDiriDataSource.getFromBackup(backupName,kavlingKode)

            emit(DataUtil.mapSingleResult(
                originResult = remoteResult,
                targetMapper = MyObjectMapper::mapDataDiri,
            ))
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            val model = mapDataDiri(dataDiri)

            // Whenever data diri added, let the kavling set "sudah Isi Data Diri"
            remoteKavlingDataSource.setKavlingBelumDiisi(kavlingKode, false)

            // Adding mechanism on Local Data Source already available on getDataDiri() method.

            emitAll(remoteDataDiriDataSource.addDataDiri(kavlingKode, model))
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            // Whenever data diri deleted, let kavling "sudah isi Data Diri" to be false
            remoteKavlingDataSource.setKavlingBelumDiisi(kavlingKode, true)

            // Also, delete the data diri on Local!
            val deleteLocal = localDataDiriDataSource.deleteDataDiri(kavlingKode)
            deleteLocal.onFailure {
                emit(Result.failure(it))
            }

            // Then, delete on the remote ...
            emitAll(remoteDataDiriDataSource.deleteDataDiri(kavlingKode))
        }
    }

    override suspend fun refreshCache(kavlings: List<Kavling>): Result<Nothing?> {
        return try {
            localDataDiriDataSource.deleteAll().getOrThrow()

            kavlings.forEach { kavling ->
                val kavlingKode = kavling.kode
                val dataDiriModel = remoteDataDiriDataSource.getDataDiri(kavlingKode).getOrThrow()

                dataDiriModel?.also {
                    localDataDiriDataSource.addDataDiri(kavlingKode, it).getOrThrow()
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    /**
     * Inden Booking related
     */
    override suspend fun getFromIndenBooking(keyId: String): Result<DataDiri?> {
        val invalidCache = checkAndInvalidateCache(
            dataDiriIndenBookingLocalTable,
            dataDiriIndenBookingRemoteTable,
            onInvalid = {
                localDataDiriDataSource.deleteAllFromIndenBooking()
            },
        )
        val localModel = localDataDiriDataSource.getFromIndenBooking(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (invalidCache || localModel == null) {
            Log.d("INDEN_BOOKING", "Data Diri on Cache was invalid or Local Data Source is null! " +
                    "Fetching from Remote Data Source now.")

            remoteDataDiriDataSource.getFromIndenBooking(keyId).getOrThrow()?.also {
                localDataDiriDataSource.insertFromIndenBooking(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Data Diri on Local Data Source is okay, returning from it.")
        }

        val refreshedLocalResult = localDataDiriDataSource.getFromIndenBooking(keyId)
        return DataUtil.mapSingleResult(
            originResult = refreshedLocalResult,
            targetMapper = MyObjectMapper::mapDataDiri,
        )
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Data Diri cache is invalid! Purging local data now.")
            localDataDiriDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Data Diri Local : ${it.message}")
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

    // For Inden Booking
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