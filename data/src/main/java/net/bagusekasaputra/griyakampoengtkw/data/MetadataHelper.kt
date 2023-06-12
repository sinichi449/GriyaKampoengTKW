package net.bagusekasaputra.griyakampoengtkw.data

import android.util.Log
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
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp
        val cacheInvalid = localTimestamp != remoteTimestamp

        Log.d("DEBUG_ME", "Table metadata \"$metadataTable\" localtimestap is $localTimestamp and the remote is $remoteTimestamp")

        if (cacheInvalid) {
            onInvalidCache()

            remoteTimestamp?.also {
                localMetadata.insert(MetadataModel(metadataTable, it))
            }
        }
    }

    suspend fun updateMetadataOnDataChange() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable) ?: MetadataModel(metadataTable, 0L)
        val newMetadata = MetadataModel(metadataTable, currentTimemillis)

        localMetadata.insert(newMetadata)
        remoteMetadata.update(oldMetadata, newMetadata)
    }

    suspend fun updateLocalMetadataOnInvalid() {
        remoteMetadata.get(metadataTable)?.also {
            localMetadata.insert(it)
        }
    }
}