package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapBlockModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository

class BlockRepositoryImpl(
    private val localBlockDataSource: LocalBlockDataSource,
    private val remoteBlockDataSource: RemoteBlockDataSource,
    private val cacheHelper: CacheHelper,
): BlockRepository {

    private val cacheTable = "blocks"

    override fun getAllBlocks(dataMode: DataMode): Flow<Result<List<Block>?>> {
        val flowOffline = flow<Result<List<Block>?>> {
           localBlockDataSource.getAllBlocks()
               .onSuccess {
                   val blocks = it?.map { model ->
                       mapBlockModel(model)
                   }

                   emit(Result.success(blocks))
               }
               .onFailure {
                   emit(Result.failure(it))
               }
        }
        val flowOnline = DataUtil.networkBoundResources(
            shouldFetch = {
                cacheHelper.checkAndInvalidateCache(
                    localTable = cacheTable,
                    remoteTable = cacheTable,
                    onInvalid = {
                        localBlockDataSource.deleteAll()
                    }
                )
            },
            query = { flowOffline.first() },
            fetch = {
                remoteBlockDataSource.getAllBlocks().map { models ->
                    models?.map {
                        mapBlockModel(it)
                    }
                }
            },
            saveFetchResult = { blocks ->
                if (blocks.isNullOrEmpty()) {
                    Result.failure(Throwable("Not found Block Models from remote data source!"))
                } else {
                    val blockModels = blocks.map {
                        mapBlockModel(it)
                    }
                    localBlockDataSource.addAll(blockModels)

                    Result.success(Unit)
                }
            }
        )
        val flowDataLama = flow<Result<List<Block>?>> {
            // TODO
            emit(Result.failure(Throwable("Data Lama for Blocks Repository doesn't yet implemented!")))
        }

        return when (dataMode) {
            DataMode.ONLINE -> flowOnline
            DataMode.OFFLINE -> flowOffline
            DataMode.DATA_LAMA -> flowDataLama
        }
    }

    override fun addBlock(block: Block): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBlockDataSource.addNewBlock(mapBlockModel(block))

            emit(remoteResult)
        }
    }

    override suspend fun refreshCache(): Result<Nothing?> {
        return try {
            localBlockDataSource.deleteAll().getOrThrow()

            val blockModels = remoteBlockDataSource.getAllBlocks().getOrThrow()
            blockModels?.also {
                localBlockDataSource.addAll(it).getOrThrow()
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

}