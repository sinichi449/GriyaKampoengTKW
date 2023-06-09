package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper.Companion.checkAndInvalidateCache
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapKavling
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class KavlingRepositoryImpl(
    private val localKavlingDataSource: LocalKavlingDataSource,
    private val remoteKavlingDataSource: RemoteKavlingDataSource,
    private val backupKavlingDataSource: BackupKavlingDataSource,
    private val localBlockDataSource: LocalBlockDataSource,
    private val cacheHelper: CacheHelper,
): KavlingRepository {

    private val remoteCacheKey = "kavling"
    private val localCacheTable = "kavling"
    private var shouldCheckCache = true

    override fun getAsFlow(blok: String): Flow<Result<Kavling?>> {
        return localKavlingDataSource.getAsFlow(blok)
            .checkAndInvalidateCache(cacheHelper, localCacheTable, remoteCacheKey,
                onInvalid = {
                    Log.d("SEQUENTIAL_KAVLING", "Kavling cache is invalid! Purging local data source ...")
                    localKavlingDataSource.deleteAll().getOrThrow()

                    Log.d("SEQUENTIAL_KAVLING", "Pulling Kavling List from Remote Data Source ...")
                    val remoteModels = remoteKavlingDataSource.getAllKavlings(blok)
                        .getOrThrow()
                    if (!remoteModels.isNullOrEmpty()) {
                        remoteModels.forEach { Log.d("SEQUENTIAL_KAVLING", "Found ${it.kode} on Remote !") }
                        localKavlingDataSource.addAll(remoteModels).getOrThrow()
                    } else {
                      Log.d("SEQUENTIAL_KAVLING", "Can't found any Kavling in Blok $blok on Remote Data Source!")
                    }
                }
            )
            .map {
                DataUtil.mapSingleResult(
                    originResult = it,
                    targetMapper = MyObjectMapper::mapKavling
                )
            }
            .catch {
                emit(Result.failure(it))
            }
    }

    override fun getKavlingByBlock(
        blockCode: String,
        dataMode: DataMode,
    ): Flow<Result<List<Kavling>?>> {
        return flow<Result<List<Kavling>?>> {
            val flowLocal = flow<Result<List<Kavling>?>> {
                // Getting from local
                val localResult = localKavlingDataSource.getKavlingByBlockKode(blockCode)

                if (localResult.isSuccess) {
                    // Emit from local
                    val mappedResult = DataUtil.mapListResult(
                        originResult = localResult,
                        targetMapper = ::mapKavling,
                    )
                    emit(mappedResult)
                } else {
                    // Emit local error
                    emit(Result.failure(Throwable("Getting kavling from both server and local failed")))
                }
            }

            // Getting from Remote means when the user connectivity somewhat interrupted,
            // I can pull the data out of Local instead. See below.
            val flowRemote = flow<Result<List<Kavling>?>> {
                // Getting kavling from server
                val remoteResult = remoteKavlingDataSource.getAllKavlings(blockCode)

                if (remoteResult.isSuccess) {
                    // Emit the kavling
                    val mappedResult = DataUtil.mapListResult(
                        originResult = remoteResult,
                        targetMapper = ::mapKavling,
                    )
                    emit(mappedResult)

                    // Then write kavling to local
                    val kavlingModels = remoteResult.getOrNull()?.map { mapKavling(it) }
                    kavlingModels?.forEach {
                        localKavlingDataSource.addKavling(blockCode, mapKavling(it))
                    }
                } else {
                    // Emit the error
                    remoteResult.exceptionOrNull()?.let { emit(Result.failure(it)) }

                    emitAll(flowLocal)
                }
            }

            when (dataMode) {
                DataMode.OFFLINE -> emitAll(flowLocal)
                DataMode.ONLINE -> emitAll(flowRemote)
                DataMode.DATA_LAMA -> {
                    emit(DataUtil.mapListResult(
                        originResult = backupKavlingDataSource.getKavlingByBlockKode(
                            blockCode
                        ), targetMapper = {
                            mapKavling(it)
                        }))
                }
            }
        }
    }

    override fun getAllKavlings(blockKodes: List<String>): Flow<Result<HashMap<String, List<Kavling>>?>> {
        return flow {
            val mapKavling = HashMap<String, List<Kavling>>()

            blockKodes.forEach {
                try {
                    val kavlingPerBlok = getKavlingByBlock(it, DataMode.ONLINE)
                        .first().getOrThrow()

                    if (kavlingPerBlok?.isNotEmpty() == true)
                        mapKavling[it] = kavlingPerBlok
                } catch (e: Exception) {
                    emit(Result.failure(e))
                }
            }

            emit(Result.success(mapKavling))
        }
    }

    override fun addKavling(blockKode: String, kavling: Kavling): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteKavlingDataSource.addKavling(blockKode, mapKavling(kavling))

            emit(remoteResult)
        }
    }

    override fun updateKavling(
        blockCode: String,
        oldKavling: Kavling,
        newKavling: Kavling
    ): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteKavlingDataSource.updateKavling(
                blockKode = blockCode,
                oldKavling = mapKavling(oldKavling),
                newKavling = mapKavling(newKavling),
            )

            emit(remoteResult)
        }
    }

    override fun removeKavling(blockCode: String, kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // We need to delete the local data too
            val localResult = localKavlingDataSource.deleteKavling(kavlingKode)

            localResult.onFailure { throwable ->
                emit(Result.failure(throwable))
            }

            val remoteResult = remoteKavlingDataSource.deleteKavling(blockCode, kavlingKode)

            emit(remoteResult)
        }
    }
    override fun getUnmigratedKavlings(backupName: String): Flow<Result<List<String>?>> {
        return flow {
            emit(remoteKavlingDataSource.getUnmigratedKavlings(backupName))
        }
    }

    override suspend fun getRekapExclusionList(): Result<List<String>?> {
        return remoteKavlingDataSource.getRekapExclusionList()
    }

    override suspend fun refreshCache(blocks: List<Block>): Result<Nothing?> {
        return try {
            localKavlingDataSource.deleteAll().getOrThrow()

            // If blocks parameter is empty, get from cache
            val mBlocks: List<Block> = blocks.ifEmpty {
                val cachedBlockModels = localBlockDataSource.getAllBlocks().getOrThrow()
                val mappedBlockModels = cachedBlockModels?.map { MyObjectMapper.mapBlockModel(it) }

                mappedBlockModels ?: blocks
            }.ifEmpty {
                throw Exception("Can't get blocks because the parameter and cached blocks are both empty!")
            }

            mBlocks.forEach { block ->
                val kavlingModels = remoteKavlingDataSource.getAllKavlings(block.kode).getOrThrow()

                kavlingModels?.also {
                    localKavlingDataSource.addAll(it)
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

}