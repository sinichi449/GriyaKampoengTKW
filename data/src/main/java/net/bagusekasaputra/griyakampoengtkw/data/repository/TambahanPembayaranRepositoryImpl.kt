package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class TambahanPembayaranRepositoryImpl(
    private val localDataSource: LocalTambahanPembayaranDataSource,
    private val remoteDataSource: RemoteTambahanPembayaranDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val cacheHelper: CacheHelper,
): TambahanPembayaranRepository {

    private val cacheTable = "tambahanPembayaran"
    private var shouldCheckCache = true

    // Invalidate and update metadata

    override suspend fun getAllByKavling(kavling: String): Result<List<TambahanPembayaran>?> {
        val isInvalidCache = cacheIsPurged()
        if (isInvalidCache) {
            val remoteModels = remoteDataSource.getAll(kavling)
                .getOrThrow()
            if (!remoteModels.isNullOrEmpty()) {
                localDataSource.insertAll(remoteModels).getOrThrow()
            }
        }

        // Second to anticipate when modelLocal is empty or null
        return localDataSource.getAll(kavling).map { result ->
            if (result.isNullOrEmpty()) null
            else result.map { MyObjectMapper.mapTambahanPembayaran(it) }
        }
    }

    override suspend fun getById(kavling: String, id: String): Result<TambahanPembayaran?> {
        val isInvalidCache = cacheIsPurged()
        if (isInvalidCache) {
            val remoteModel = remoteDataSource.getById(kavling, id).getOrThrow()
            if (remoteModel != null) {
                localDataSource.insert(remoteModel).getOrThrow()
            }
        }

        return localDataSource.getById(kavling, id).map { result ->
            result?.let { MyObjectMapper.mapTambahanPembayaran(it) }
        }
    }

    override suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?> {
        updateMetadata()

        val model = MyObjectMapper.mapTambahanPembayaran(tambahanPembayaran)
        return remoteDataSource.insert(model)
            .onSuccess {
                localDataSource.insert(model)
            }
    }

    override suspend fun update(
        kavling: String,
        id: String,
        newData: TambahanPembayaran
    ): Result<Nothing?> {
        val newModel = MyObjectMapper.mapTambahanPembayaran(newData)

        return remoteDataSource.update(kavling, id, newModel)
    }

    override suspend fun delete(kavling: String, id: String): Result<Nothing?> {
        return remoteDataSource.delete(kavling, id)
    }

    private suspend fun cacheIsPurged(): Boolean {
        return if (shouldCheckCache) {
            cacheHelper.checkAndInvalidateCache(
                cacheTable, cacheTable,
                onInvalid = { localDataSource.deleteAll() }
            )

            shouldCheckCache = false

            true
        } else {
            false
        }
    }

    private suspend fun updateMetadata() {
        val currentTimeMillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(cacheTable) ?: MetadataModel(cacheTable, 0L)
        val newMetadata = MetadataModel(cacheTable, currentTimeMillis)

        localMetadata.insert(newMetadata)
        remoteMetadata.update(oldMetadata, newMetadata)
    }
}