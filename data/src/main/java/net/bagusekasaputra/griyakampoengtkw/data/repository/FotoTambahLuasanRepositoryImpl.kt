package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository

class FotoTambahLuasanRepositoryImpl(
    private val localSource: LocalFotoTambahLuasanDataSource,
    private val remoteSource: RemoteFotoTambahLuasanDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): FotoTambahLuasanRepository {

    private val metadataTable = "image_foto_tambah_luasan"
    private var hasMetadataChecked = false

    override fun get(kavling: String, tambahLuasanId: String): Flow<Result<FotoTambahLuasan?>> {
        return flow {
            if (!hasMetadataChecked) {
                val localTimestamp = localMetadata.get(metadataTable)?.timestamp
                val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp
                val cacheInvalid = localTimestamp != remoteTimestamp

                if (cacheInvalid) {
                    localSource.deleteAll()
                    localMetadata.insert(MetadataModel(metadataTable, remoteTimestamp!!))
                }

                hasMetadataChecked = true
            }

            val localModel = localSource.get(kavling, tambahLuasanId).getOrNull()
            if (localModel == null) {
                Log.d("DEBUG_ME", "FotoTambahLuasan will be retrived from Remote Data Source")
                remoteSource.get(kavling, tambahLuasanId)
                    .onSuccess {
                        if (it != null) {
                            emit(Result.success(MyObjectMapper.mapFotoTambahLuasan(it)))
                        } else {
                            emit(Result.success(null))
                        }
                    }
                    .onFailure {
                        emit(Result.failure(it))
                    }
            } else {
                Log.d("DEBUG_ME", "FotoTambahLuasan retrived from Local Data Source")
                emit(Result.success(MyObjectMapper.mapFotoTambahLuasan(localModel)))
            }
        }
    }

    override fun insert(entity: FotoTambahLuasan): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val model = MyObjectMapper.mapFotoTambahLuasan(entity)
            localSource.insert(model).getOrThrow()
            remoteSource.insert(model)
                .onSuccess {
                    emit(Result.success(null))
                }
                .onFailure {
                    emit(Result.failure(it))
                }
        }
    }

    override fun delete(kavling: String, tambahLuasanId: String): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            localSource.delete(kavling, tambahLuasanId).getOrThrow()
            remoteSource.delete(kavling, tambahLuasanId)
                .onSuccess {
                    emit(Result.success(null))
                }
                .onFailure {
                    emit(Result.failure(it))
                }
        }
    }

    private suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldTimestamp = localMetadata.get(metadataTable)?.timestamp ?: 0L

        remoteMetadata.update(
            oldMetadataModel = MetadataModel(metadataTable, oldTimestamp),
            newMetadataModel = MetadataModel(metadataTable, currentTimemillis),
        )
        localMetadata.insert(
            MetadataModel(metadataTable, currentTimemillis)
        )
    }
}