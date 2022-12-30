package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class FeeMarketingRepositoryImpl(
    private val localFeeMarketingDataSource: LocalFeeMarketingDataSource,
    private val remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): FeeMarketingRepository {

    private val metadataTable = "feeMarketing"

    override fun getBatch(listKavling: List<String>): Flow<Result<Map<String, FeeMarketing?>?>> {
        return flow {
            checkCache()

            val batchFeeMarketing = mutableMapOf<String, FeeMarketing?>()

            listKavling.forEach { kavling ->
                val localModel = localFeeMarketingDataSource.getByKavlingKode(kavling)
                    .onFailure {
                        Log.d("DEBUG_ME", "FeeMarketingRepoImpl:35 onFailure -> ${it.message}")
                    }
                    .getOrNull()

                if (localModel == null) {
                    remoteFeeMarketingDataSource.getByKavlingKode(kavling)
                        .onSuccess { model ->
                            model?.let {
                                localFeeMarketingDataSource.addFeeMarketing(kavling, it)
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FeeMarketingRepoImpl:45 onFailure -> ${it.message}")
                        }

                    // Second try
                    localFeeMarketingDataSource.getByKavlingKode(kavling)
                        .onSuccess {
                            batchFeeMarketing[kavling] = if (it != null) mapFeeMarketing(it) else null
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FeeMarketingRepoImpl:56 onFailure -> ${it.message}")
                        }
                } else {
                    batchFeeMarketing[kavling] = mapFeeMarketing(localModel)
                }
            }

            emit(Result.success(batchFeeMarketing))
        }
    }

    override fun getByKavlingKode(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<FeeMarketing?>> {
        return flow {
            val flowOffline = flow {
                val localResult = localFeeMarketingDataSource.getByKavlingKode(kavlingKode)

                emit(
                    DataUtil.mapSingleResult(localResult, ::mapFeeMarketing)
                )
            }

            val flowOnline = flow<Result<FeeMarketing?>> {
                // First, get from remote server
                val remoteResult = remoteFeeMarketingDataSource.getByKavlingKode(kavlingKode)

                remoteResult.onSuccess {
                    // If success, firstly write the data into local data source
                    val feeMarketingModel = remoteResult.getOrNull()
                    if (feeMarketingModel != null) {
                        val localWrite = localFeeMarketingDataSource.addFeeMarketing(kavlingKode, feeMarketingModel)
                        localWrite.onFailure {
                            emit(Result.failure(it))
                        }
                    }

                    // Then, emit the result
                    val mappedResult = DataUtil.mapSingleResult(
                        originResult = remoteResult,
                        targetMapper = ::mapFeeMarketing,
                    )
                    emit(mappedResult)
                }

                remoteResult.onFailure {
                    // If fails, then emit the error
                    emit(Result.failure(it))

                    // Then, emit from local data source instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<FeeMarketing?>> {
        return flow {
            // First, get from remote server
            val remoteResult = remoteFeeMarketingDataSource.getByKavlingKode(kavlingKode)

            val mappedResult = DataUtil.mapSingleResult(
                originResult = remoteResult,
                targetMapper = ::mapFeeMarketing,
            )
            emit(mappedResult)
        }
    }

    override fun addFeeMarketing(feeMarketing: FeeMarketing): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val remoteResult = remoteFeeMarketingDataSource.addFeeMarketing(
                kavlingKode = feeMarketing.kavlingKode,
                feeMarketingModel = mapFeeMarketing(feeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun updateFeeMarketing(
        oldFeeMarketing: FeeMarketing,
        newFeeMarketing: FeeMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            // We need to update the local data source too
            val localUpdate = localFeeMarketingDataSource.updateFeeMarketing(
                kavlingKode = oldFeeMarketing.kavlingKode,
                oldFeeMarketingModel = mapFeeMarketing(oldFeeMarketing),
                newFeeMarketingModel = mapFeeMarketing(newFeeMarketing),
            )
            localUpdate.onFailure {
                emit(Result.failure(it))
            }


            val remoteResult = remoteFeeMarketingDataSource.updateFeeMarketing(
                kavlingKode = oldFeeMarketing.kavlingKode,
                oldFeeMarketingModel = mapFeeMarketing(oldFeeMarketing),
                newFeeMarketingModel = mapFeeMarketing(newFeeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val localDelete = localFeeMarketingDataSource.deleteFeeMarketing(kavlingKode)
            localDelete.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteFeeMarketingDataSource.deleteFeeMarketing(kavlingKode)

            emit(remoteResult)
        }
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Fee Marketing cache is invalid! Purging local data now.")
            localFeeMarketingDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Fee Marketing Local : ${it.message}")
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

    private fun mapFeeMarketing(feeMarketingModel: FeeMarketingModel): FeeMarketing =
        feeMarketingModel.let {
            FeeMarketing(
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toString(),
                tanggalPenerimaan = it.getTanggalStr(),
            )
        }

    private fun mapFeeMarketing(feeMarketing: FeeMarketing) =
        feeMarketing.let {
            FeeMarketingModel(
                timeMillis = it.getTimemillisTanggalPenerimaan(),
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toLong(),
            )
        }

}