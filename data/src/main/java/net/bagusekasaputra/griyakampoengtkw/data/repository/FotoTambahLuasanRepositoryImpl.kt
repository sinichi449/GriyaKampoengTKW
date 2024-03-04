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

//            emit(
//                remoteSource.get(kavling, tambahLuasanId).map { model ->
//                    if (model != null) MyObjectMapper.mapFotoTambahLuasan(model)
//                    else null
//                }
//            )
        }
    }

}