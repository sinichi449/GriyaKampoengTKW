package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository

class HargaKavlingRepositoryImpl(
    private val localHargaKavlingDataSource: LocalHargaKavlingDataSource,
    private val remoteHargaKavlingSource: RemoteHargaKavlingSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): HargaKavlingRepository {

    private val metadataTable = "hargaKavling"

    override fun getBatch(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>> {
        return flow {
            checkCache()

            val mapHargaKavling = mutableMapOf<String, HargaKavling?>()

            listKavling.forEach { kavling ->
                val localModel = localHargaKavlingDataSource.getHargaKavlingModel(kavling)
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED GET Harga Kavling $kavling Local HargaKavlingRepoImpl:39 : ${it.message}")
                    }
                    .getOrNull()

                if (localModel == null) {
                    remoteHargaKavlingSource.getHargaKavlingModel(kavling)
                        .onSuccess { model ->
                            model?.let {
                                localHargaKavlingDataSource.addHargaKavlingModel(it)
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "HargaKavlingRepoImpl:49 Failure -> ${it.message}")
                        }

                    // Second try
                    localHargaKavlingDataSource.getHargaKavlingModel(kavling)
                        .onSuccess {
                            mapHargaKavling[kavling] = if (it != null) mapHargaKavling(it) else null
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "HargaKavlingRepoImpl:60 onFailure -> ${it.message}")
                        }
                } else {
                    mapHargaKavling[kavling] = mapHargaKavling(localModel)
                }
            }

            emit(Result.success(mapHargaKavling))
        }
    }

    override fun getHargaKavling(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<HargaKavling?>> {
        return flow {
            // TODO: Check metadata

            val flowOffline = flow<Result<HargaKavling?>> {
                val localResult = localHargaKavlingDataSource.getHargaKavlingModel(kavlingKode)

                emit(
                    DataUtil.mapSingleResult(localResult, ::mapHargaKavling)
                )
            }

            val flowOnline = flow<Result<HargaKavling?>> {
                // First get from remote
                val remoteResult = remoteHargaKavlingSource.getHargaKavlingModel(kavlingKode)

                remoteResult.onSuccess {
                    // Then we write it to the local data source
                    if (it != null) {
                        val writeLocal = localHargaKavlingDataSource.addHargaKavlingModel(it)
                        writeLocal.onFailure { errorLocal ->
                            emit(Result.failure(errorLocal))
                        }
                    }

                    // Finally, emit the result
                    emit(
                        DataUtil.mapSingleResult(remoteResult, ::mapHargaKavling)
                    )
                }

                remoteResult.onFailure {
                    // When error, firstly emit the cause
                    emit(Result.failure(it))

                    // Then, get from local instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }


    override fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            val mapHargaKavling = mapHargaKavling(hargaKavling)
            val remoteResult = remoteHargaKavlingSource.addHargaKavlingModel(mapHargaKavling)

            remoteResult.onSuccess {
                emit(Result.success(true))
            }

            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }


    override fun deleteHargaKavling(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            // We need to delete the data on the local data source too
            val localDelete = localHargaKavlingDataSource.deleteHargaKavlingModel(kavlingKode)
            localDelete.onFailure {
                emit(Result.failure(it))
            }

            val remoteDelete = remoteHargaKavlingSource.deleteHargaKavlingModel(kavlingKode)

            emit(remoteDelete)
        }
    }

    override fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<HargaKavling> {
        return callbackFlow {
            remoteHargaKavlingSource.getSingleHargaKavlingForPembayaran(
                kavlingKode = kavlingKode
            ) {
                if (it != null) {
                    trySendBlocking(mapHargaKavling(it))
                } else {
                    trySendBlocking(HargaKavling(kavlingKode, "0", "0"))
                }
            }

            awaitClose {  }
        }
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Harga Kavling cache is invalid! Purging local data now.")
            localHargaKavlingDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Harga Kavling Local : ${it.message}")
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

    private fun mapHargaKavling(hargaKavling: HargaKavling): HargaKavlingModel {
        return hargaKavling.let {
            HargaKavlingModel(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatStringToLong(it.harga),
                tambahLuasan = NumberUtil.formatStringToLong(it.tambahanLuas),
            )
        }
    }

    private fun mapHargaKavling(hargaKavlingModel: HargaKavlingModel): HargaKavling {
        return hargaKavlingModel.let {
            HargaKavling(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatLongToString(it.harga),
                tambahanLuas = NumberUtil.formatLongToString(it.tambahLuasan),
            )
        }
    }

}