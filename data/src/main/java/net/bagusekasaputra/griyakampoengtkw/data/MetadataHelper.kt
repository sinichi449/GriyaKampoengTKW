package net.bagusekasaputra.griyakampoengtkw.data

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

class MetadataHelper(
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val metadataTable: String,
) {

    suspend fun checkCache(onInvalidCache: suspend () -> Unit) {
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            onInvalidCache()

            localMetadata.insert(
                MetadataModel(metadataTable, remoteTimestamp)
            )
        }
    }

    suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable) ?: MetadataModel(metadataTable, 0L)
        val newMetadata = MetadataModel(metadataTable, currentTimemillis)

        localMetadata.insert(newMetadata)
        remoteMetadata.update(oldMetadata, newMetadata)
    }
}