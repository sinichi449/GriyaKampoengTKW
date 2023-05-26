package net.bagusekasaputra.griyakampoengtkw.data

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

class CacheHelper(
    private val localMetadataDataSource: LocalMetadataDataSource,
    private val remoteMetadataDataSource: RemoteMetadataDataSource,
) {

    suspend fun checkAndInvalidateCache(
        localTable: String,
        remoteTable: String,
        onInvalid: suspend () -> Unit,
    ): Boolean {
        var isInvalid = false

        val localTimestamp = localMetadataDataSource.get(localTable)?.timestamp
        val remoteTimestamp = remoteMetadataDataSource.get(remoteTable)?.timestamp
        if (localTimestamp != remoteTimestamp) {
            isInvalid = true

            onInvalid()

            remoteTimestamp?.also {
                localMetadataDataSource.insert(MetadataModel(localTable, it))
            }
        }

        return isInvalid
    }

}