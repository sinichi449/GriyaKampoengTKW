package net.bagusekasaputra.griyakampoengtkw.data

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
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

    suspend fun updateMetadata(
        localTable: String,
        remoteTable: String,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val currentTimemillis = System.currentTimeMillis()
            val newLocalMetadata = MetadataModel(localTable, currentTimemillis)
            val newRemoteMetadata = MetadataModel(remoteTable, currentTimemillis)

            remoteMetadataDataSource.update(newRemoteMetadata, newRemoteMetadata)
            localMetadataDataSource.insert(newLocalMetadata)

            trySendBlocking(Result.success(null))

            awaitClose {  }
        }.first()
    }

    companion object {
        fun <T> Flow<Result<T>>.checkAndInvalidateCache(
            cacheHelper: CacheHelper,
            localTable: String,
            remoteTable: String,
            onInvalid: suspend () -> Unit,
        ): Flow<Result<T>> {
            return this.onStart {
                cacheHelper.checkAndInvalidateCache(localTable, remoteTable, onInvalid)
            }
        }
    }
}