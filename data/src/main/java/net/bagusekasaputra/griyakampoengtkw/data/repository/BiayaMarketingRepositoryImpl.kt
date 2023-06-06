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
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapBiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository

class BiayaMarketingRepositoryImpl(
    private val localBiayaMarketingDataSource: LocalBiayaMarketingDataSource,
    private val remoteBiayaMarketingDataSource: RemoteBiayaMarketingDataSource,
    private val backupBiayaMarketingDataSource: BackupBiayaMarketingDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): BiayaMarketingRepository {

    private val metadataTable = "biayaMarketing"

    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>?>> {
        return flow {
            checkCache()

            val batchBiayaMarketing = mutableMapOf<String, List<BiayaMarketing>?>()

            listKavling.forEach { kavling ->
                val localModel = localBiayaMarketingDataSource.getAllBiayaMarketing(kavling)
                    .map {
                        return@map it?.values?.toList()
                    }
                    .onFailure {
                        Log.d("DEBUG_ME", "BiayaMarketingRepoImpl:39 onFailure -> ${it.message}")
                    }
                    .getOrNull()

                if (localModel.isNullOrEmpty()) {
                    remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavling)
                        .onSuccess { listModel ->
                            listModel?.forEach {
                                localBiayaMarketingDataSource.addBiayaMarketing(kavling, it)
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "BiayaMarketingRepoImpl:49 onFailure -> ${it.message}")
                        }

                    // Second try
                    localBiayaMarketingDataSource.getAllBiayaMarketing(kavling)
                        .map {
                            return@map it?.values?.toList()
                        }
                        .onSuccess {
                            batchBiayaMarketing[kavling] = it?.map { model -> mapBiayaMarketing(model) }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "BiayaMarketingRepoImpl:60 onFailure -> ${it.message}")
                        }
                } else {
                    batchBiayaMarketing[kavling] = localModel.map { mapBiayaMarketing(it) }
                }
            }

            emit(Result.success(batchBiayaMarketing))
        }
    }

    override fun getBatchOffline(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>>?>> {
        return flow {
            val batchBiayaMarketing = mutableMapOf<String, List<BiayaMarketing>>()

            listKavling.forEach { kavling ->
                val listBiayaMarketing = mutableListOf<BiayaMarketing>()

                localBiayaMarketingDataSource.getAllBiayaMarketing(kavling)
                    .onSuccess {
                        it?.forEach { item ->
                            listBiayaMarketing.add(mapBiayaMarketing(item.value))
                        }
                    }
                    .onFailure {
                        Log.d("DEBUG_ME", "BiayaMarketingRepoImpl:81 onFailure -> $it")
                        emit(Result.failure(it))
                    }

                if (listBiayaMarketing.isEmpty().not())
                    batchBiayaMarketing[kavling] = listBiayaMarketing
            }

            emit(Result.success(
                batchBiayaMarketing.ifEmpty { null }
            ))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>>> {
        return callbackFlow {
            try {
                val mapListBiayaMarketingLama = mutableMapOf<String, List<BiayaMarketing>?>()
                listKavling.forEach { kavlingLama ->
                    mapListBiayaMarketingLama[kavlingLama] = getAllByKavlingKode(kavlingLama, DataMode.DATA_LAMA).first().getOrThrow()
                }

                trySendBlocking(Result.success(mapListBiayaMarketingLama))
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }

    override fun fromBackupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, List<BiayaMarketing>?>?>> {
        // TODO
        return flow {
            val mapBiayaMarketing = mutableMapOf<String, List<BiayaMarketing>?>()

            listKavling.forEach { kavling ->
                val remoteResult = remoteBiayaMarketingDataSource.getFromBackup(backupName, kavling)
                if (remoteResult.isSuccess) {
                    val listModel = remoteResult.getOrNull()
                    val listBiayaMarketing = listModel?.map { MyObjectMapper.mapBiayaMarketing(it) }

                    mapBiayaMarketing[kavling] = listBiayaMarketing
                } else {
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("Unknown Error Backup \"$backupName\" getting List Biaya Marketing at kavling $kavling")
                    errorCause.printStackTrace()

                    emit(Result.failure(errorCause))
                }
            }

            emit(Result.success(mapBiayaMarketing))
        }
    }

    override fun getAllByKavlingKode(
        kavlingKode: String,
        dataMode: DataMode,
    ): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val flowOffline = flow<Result<List<BiayaMarketing>?>> {
                val localResult = localBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                localResult.onSuccess { biayaMarketingWithId ->
                    // Here we will parse the data from local data source from Map<Id, BiayaMarketing>
                    // to List<BiayaMarketing>. We need the id because it helps the write operation
                    // such as update and delete.

                    val mappedResult = mutableListOf<BiayaMarketing>()
                    biayaMarketingWithId?.keys?.forEach { id ->
                        val model = biayaMarketingWithId[id]

                        model?.let {
                            mappedResult.add(
                                BiayaMarketing(
                                    id = id,
                                    tanggal = it.tanggal,
                                    kavlingKode = it.kavlingKode,
                                    jenisBiaya = it.jenisBiaya,
                                    harga = it.harga.toString()
                                )
                            )
                        }
                    }

                    if (mappedResult.isEmpty())
                        emit(Result.success(null))
                    else
                        emit(Result.success(mappedResult))
                }

                localResult.onFailure {
                    emit(Result.failure(it))
                }
            }
            val flowOnline = flow<Result<List<BiayaMarketing>?>> {
                val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                remoteResult.onSuccess {
                    // save to local data source
                    val models = remoteResult.getOrNull()
                    models?.forEach {
                        localBiayaMarketingDataSource.addBiayaMarketing(kavlingKode, it)
                    }

                    // emit from local to preserve the id
                    emitAll(flowOffline)
                }

                remoteResult.onFailure {
                    emit(Result.failure(it))

                    // on failure, emit from local
                    emitAll(flowOffline)
                }
            }
            val flowDataLama = flow<Result<List<BiayaMarketing>>> {
                backupBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)
                    .onSuccess {
                        emit(DataUtil.mapListResult(
                            originResult = Result.success(it),
                            targetMapper = ::mapBiayaMarketing,
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

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

            emit(DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = ::mapBiayaMarketing,
            ))
        }
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val remoteResult = remoteBiayaMarketingDataSource.addBiayaMarketing(
                kavlingKode = biayaMarketing.kavlingKode,
                biayaMarketingModel = mapBiayaMarketing(biayaMarketing)
            )
            emit(remoteResult)
        }
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val localResult = localBiayaMarketingDataSource.update(
                id = oldBiayaMarketing.id ?: -1L,
                newBiayaMarketingModel = mapBiayaMarketing(newBiayaMarketing),
            )
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.update(
                kavlingKode = oldBiayaMarketing.kavlingKode,
                oldBiayaMarketingModel = mapBiayaMarketing(oldBiayaMarketing),
                newBiayaMarketingModel = mapBiayaMarketing(newBiayaMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun deleteSingle(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val localResult = localBiayaMarketingDataSource.deleteSingle(biayaMarketing.id ?: -1L)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.deleteSingle(
                kavlingKode = kavlingKode,
                biayaMarketingModel = mapBiayaMarketing(biayaMarketing)
            )

            emit(remoteResult)
        }
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val localResult = localBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)

            emit(remoteResult)
        }
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Biaya Marketing cache is invalid! Purging local data now.")
            localBiayaMarketingDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Biaya Marketing Local : ${it.message}")
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

}