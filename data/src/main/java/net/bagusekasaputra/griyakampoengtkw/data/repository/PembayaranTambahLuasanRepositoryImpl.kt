package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class PembayaranTambahLuasanRepositoryImplD(
    private val localSource: LocalPembayaranTambahLuasanDataSource,
    private val remoteSource: RemotePembayaranTambahLuasanDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): PembayaranTambahLuasanRepository {

    private val metadataTable = "pembayaranTambahLuasan"

    override fun getAll(kavling: String): Flow<Result<List<PembayaranTambahLuasan>?>> {
        return flow {
            emit(remoteSource.getAll(kavling).map { models ->
                models?.map {
                    MyObjectMapper.mapTambahLuasanPembayaran(it)
                }
            })
            // Cache Validation
//            val localTimestamp = localMetadata.get(metadataTable)?.timestamp ?: 0L
//            val remoteTimestamp = remoteMetadata.get(metadataTable)!!.timestamp
//            val cacheInvalid = localTimestamp != remoteTimestamp
//
//            if (cacheInvalid) {
//                Log.d("DEBUG_ME", "Pembayaran Tambah Luasan cache is INVALID! Purging all local data now ...")
//                localSource.deleteAll()
//                    .onFailure {
//                        Log.d("DEBUG_ME", "FAILED attempt for purging PembayaranTambahLUasan local data source: ${it.message}")
//                    }
//
//                // inserting new timestamp to local data source
//                localMetadata.insert(MetadataModel(metadataTable, remoteTimestamp))
//            }
//
//            // Emitting result
//            val localModel = localSource.getAll()
//                .getOrThrow()
//            if (localModel.isNullOrEmpty()) {
//                Log.d("DEBUG_ME", "Pembayaran Tambah Luasan Local Data Source is empty! Querying from Remote Data Source now ...")
//                remoteSource.getAll(kavling)
//                    .onSuccess {
//                        it?.let { models ->
//                            Log.d("DEBUG_ME", "Inserting non-null List Pembayaran Tambahan Luasan into Local Data Source now.")
//                            localSource.insertAll(models)
//                                .onFailure {
//                                    Log.d("DEBUG_ME", "Failed Inserting List Pembayaran Tambahan Luasan into Local Data Source: ${it.message}")
//                                }
//                        }
//                    }
//                    .onFailure {
//                        emit(Result.failure(it))
//                    }
//
//                // Second Try
//                emit(
//                    localSource.getAll().map { models ->
//                        models?.map {
//                            MyObjectMapper.mapTambahLuasanPembayaran(it)
//                        }
//                    }
//                )
//            } else {
//                Log.d("DEBUG_ME", "Pembayaran Tambahan Local Data Source is okay! Emitting from local data source ...")
//                emit(Result.success(localModel.map {
//                    MyObjectMapper.mapTambahLuasanPembayaran(it)
//                }))
//            }
        }
    }

    override fun add(pembayaranTambahLuasan: PembayaranTambahLuasan): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val model = MyObjectMapper.mapTambahLuasanPembayaran(pembayaranTambahLuasan)
            val remoteResult = remoteSource.add(model)
            remoteResult.onSuccess {
                // TODO: Insert to local data source too
                emit(Result.success(null))
            }

            remoteResult.onFailure {
                emit(Result.failure(it))
            }
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