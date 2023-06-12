package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapBiayaLain
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class BiayaLainRepositoryImpl(
    private val localBiayaLainDataSource: LocalBiayaLainDataSource,
    private val remoteBiayaLainDataSource: RemoteBiayaLainDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): BiayaLainRepository {

    private val metadataTable = "biayaLain"

    override fun getAllOnline(dataMode: DataMode): Flow<Result<List<BiayaLain>?>> {
        return flow {
            // Cache Validation
            val localTimestamp = localMetadata.get(metadataTable)
                ?.timestamp
            val remoteTimestamp = remoteMetadata.get(metadataTable)!!
                .timestamp
            val cacheInvalid = localTimestamp != remoteTimestamp

            if (cacheInvalid) {
                Log.d("DEBUG_ME", "Biaya Lain metadata cache is INVALID! Purging all local Biaya Lain in Local Data Source ...")
                localBiayaLainDataSource.deleteAll()
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED attempt for Purging Biaya Lain Data Source: ${it.message}")
                    }
                localMetadata.insert(
                    MetadataModel(metadataTable, remoteTimestamp)
                )
            }

            // Emitting result
            val localModel = localBiayaLainDataSource.getAll()
                .first()
                .getOrThrow()

            if (localModel.isNullOrEmpty()) {
                Log.d("DEBUG_ME", "Biaya Lain on Local Data Source is Empty! Querying them from Remote Data Source now.")
                remoteBiayaLainDataSource.getAll()
                    .first()
                    .onSuccess { listModel ->
                        listModel?.let {
                            Log.d("DEBUG_ME", "Inserting non-null List Biaya Lain into Local Data Source now.")
                            localBiayaLainDataSource.insertAll(listModel)
                                .onFailure {
                                    Log.d("DEBUG_ME", "FAILED attempt to inserting List Biaya Lain from remote into Local : ${it.message}")
                                }
                        }
                    }
                    .onFailure {
                        emit(Result.failure(it))
                    }

                // Second Try
                emitAll(
                    localBiayaLainDataSource.getAll().map { result ->
                        result.map { listModel ->
                            listModel?.map {
                                mapBiayaLain(it)
                            }
                        }
                    }
                )
            } else {
                Log.d("DEBUG_ME", "Biaya Lain Local Data Source is Okay! Emitting from local data source ...")
                emit(Result.success(
                    localModel.map {
                        mapBiayaLain(it)
                    }
                ))
            }
        }
    }

    override fun getAllOffline(): Flow<Result<List<BiayaLain>?>> {
        return flow {
            emitAll(
                localBiayaLainDataSource.getAll().map { result ->
                    result.map { listModel ->
                        listModel?.map {
                            mapBiayaLain(it)
                        }
                    }
                }
            )
        }
    }

    override fun getFromBackup(): Flow<Result<List<BiayaLain>?>> {
        TODO("Not yet implemented")
    }

    override fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>> {
        /**
         * Assuming the UI goes to getAll() method first, we need not to check
         * the metadata again. So, we need to emit only from Local Data Source.
         */
        return flow {
            emitAll(
                localBiayaLainDataSource.getSingle(jenisBiaya).map { result ->
                    result.map { model ->
                        model?.let {
                            mapBiayaLain(it)
                        }
                    }
                }
            )
        }
    }

    override fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val remoteResult = remoteBiayaLainDataSource.addBiaya(mapBiayaLain(biayaLain))
            remoteResult.onSuccess {
                localBiayaLainDataSource.insert(mapBiayaLain(biayaLain))
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED attempt to Insert single Biaya Lain into Local Data Source : ${it.message}")
                    }
            }

            emit(remoteResult)
        }
    }

    override fun updateBiayaLain(
        oldBiayaLain: BiayaLain,
        newBiayaLain: BiayaLain,
    ): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val remoteResult = remoteBiayaLainDataSource.update(
                oldModel = mapBiayaLain(oldBiayaLain),
                newModel = mapBiayaLain(newBiayaLain),
            )
            remoteResult.onSuccess {
                localBiayaLainDataSource.update(
                    oldModel = mapBiayaLain(oldBiayaLain),
                    newModel = mapBiayaLain(newBiayaLain),
                ).onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Update biaya lain into Local : ${it.message}")
                }
            }

            emit(remoteResult)
        }
    }

    override fun deleteBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val remoteResult = remoteBiayaLainDataSource.delete(mapBiayaLain(biayaLain))
            remoteResult.onSuccess {
                localBiayaLainDataSource.delete(mapBiayaLain(biayaLain))
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED attempt to Delete biaya lain into Local : ${it.message}")
                    }
            }

            emit(remoteResult)
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