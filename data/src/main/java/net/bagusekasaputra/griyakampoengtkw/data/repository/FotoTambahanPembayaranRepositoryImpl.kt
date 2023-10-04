package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository

class FotoTambahanPembayaranRepositoryImpl(
    private val localDataSource: LocalFotoTambahanPembayaranDataSource,
    private val remoteDataSource: RemoteFotoTambahanPembayaranDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): FotoTambahanPembayaranRepository {

    private val localTable = { kavling: String ->
        "${kavling}_images_tambahan_pembayaran"
    }
    private val remoteTable = { kavling: String ->
        "image_tambahan_pembayaran/${kavling}"
    }
    private var hasMetadataChecked = false

    override suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaran?> {
        return remoteDataSource.get(kavling, id).map { model ->
            model?.let { MyObjectMapper.mapFotoTambahanPembayaran(it) }
        }
    }

    override suspend fun insert(entity: FotoTambahanPembayaran): Result<Nothing?> {
        updateMetadata(entity.kavling)

        val model = MyObjectMapper.mapFotoTambahanPembayaran(entity)

        localDataSource.insert(model, false)
            .onSuccess {
                // TODO
            }
            .onFailure {
                it.printStackTrace()
            }

        return remoteDataSource.insert(model)
    }

    override suspend fun isFotoExists(kavling: String, id: String): Result<Boolean> {
        return remoteDataSource.isFotoExist(kavling, id)
    }

    private suspend fun updateMetadata(kavling: String) {
        val currentTimeMillis = System.currentTimeMillis()
        val oldTimestamp = localMetadata.get(localTable(kavling))?.timestamp ?: 0L

        remoteMetadata.update(
            oldMetadataModel = MetadataModel(remoteTable(kavling), oldTimestamp),
            newMetadataModel = MetadataModel(remoteTable(kavling), currentTimeMillis)
        )
        localMetadata.insert(MetadataModel(localTable(kavling), currentTimeMillis))
    }
}