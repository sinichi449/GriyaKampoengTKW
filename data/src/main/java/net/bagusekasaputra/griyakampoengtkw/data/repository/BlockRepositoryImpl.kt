package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository

class BlockRepositoryImpl(
    private val localBlockDataSource: LocalBlockDataSource,
    private val remoteBlockDataSource: RemoteBlockDataSource,
): BlockRepository {

    override fun getAllBlocks(offline: Boolean): Flow<Result<List<Block>?>> {
        return flow<Result<List<Block>?>> {
            val flowOffline = flow<Result<List<Block>?>> {
                // Emit blocks from local instead
                val getBlockFromLocal = localBlockDataSource.getAllBlocks()

                if (getBlockFromLocal.isSuccess) {
                    emit(DataUtil.mapListResult(getBlockFromLocal, ::mapBlockModel))
                } else {
                    emit(Result.failure(Throwable("Data block masih kosong.")))
                }
            }

            // If the online request failed, it will emit the flow from "flowOffline"
            val flowOnline = flow<Result<List<Block>?>> {
                val getBlocksFromRemote = remoteBlockDataSource.getAllBlocks()

                if (getBlocksFromRemote.isSuccess) {
                    // Emit the blocks
                    emit(DataUtil.mapListResult(getBlocksFromRemote, ::mapBlockModel))


                    // Then write block to local
                    val blockModels = getBlocksFromRemote.getOrNull()?.map { mapBlockModel(it) }

                    blockModels?.forEach {
                        localBlockDataSource.addBlock(mapBlockModel(it))
                    }
                } else {
                    // Emit the error
                    getBlocksFromRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }

                    // Emit from local instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun addBlock(block: Block): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBlockDataSource.addNewBlock(mapBlockModel(block))

            emit(remoteResult)
        }
    }

    private fun mapBlockModel(blockModel: BlockModel): Block {
        return blockModel.let {
            Block(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }

    private fun mapBlockModel(block: Block): BlockModel {
        return block.let {
            BlockModel(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }
}