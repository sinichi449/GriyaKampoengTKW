package net.bagusekasaputra.griyakampoengtkw.data

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable
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

    /**
     * Compare between two [MetadataModel.timestamp] which fetched from [cacheableLocal] and [cacheableRemote].
     *
     * When they are different, automatically overwrite the local [MetadataModel] by invoking
     * [LocalMetadataDataSource.insert] just after [onInvalid] call.
     *
     * @return [Boolean] `true` when the two of [MetadataModel.timestamp] is unequal.
     * @throws IllegalStateException when invoking [Cacheable.getTableName] returns an empty [String].
     * @throws IllegalStateException if either [cacheableLocal] or [cacheableRemote] Metadata table are either `null`
     * or empty.
     */
    suspend fun checkAndInvalidateCache(
        cacheableLocal: Cacheable,
        cacheableRemote: Cacheable,
        onInvalid: suspend () -> Unit,
    ): Boolean {
        val tableLocal = cacheableLocal.getTableName()
        val tableRemote = cacheableRemote.getTableName()
        if (tableLocal.isEmpty() || tableRemote.isEmpty()) {
            throw IllegalStateException("Remote or Local Metadata table is Empty or NULL!")
        }

        val metadataLocal = localMetadataDataSource.get(tableLocal)
        val metadataRemote = remoteMetadataDataSource.get(tableRemote)
            ?: throw IllegalStateException("MetadataModel on Remote is Empty or NULL")

        val localTimestamp = metadataLocal?.timestamp
        val remoteTimestamp = metadataRemote.timestamp
        var isInvalid = false

        if (localTimestamp != remoteTimestamp) {
            isInvalid = true

            onInvalid()

            // Update local metadata
            val newLocalMetadata = MetadataModel(tableLocal, remoteTimestamp)
            localMetadataDataSource.insert(newLocalMetadata)
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