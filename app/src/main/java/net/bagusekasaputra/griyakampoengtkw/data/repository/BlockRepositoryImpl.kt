package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.LocalBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.RemoteBlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRepositoryImpl @Inject constructor(
    private val localBlockRepository: LocalBlockRepository,
    private val remoteBlockRepository: RemoteBlockRepository,
): BlockRepository {

    override fun getAllBlocks(): Flow<Result<List<Block>?>> {
        return flow<Result<List<Block>?>> {
            logEvent("Getting blocks from server ...")
            val getBlocksFromRemote = remoteBlockRepository.getAllBlocks()

            if (getBlocksFromRemote.isSuccess) {
                // Emit the blocks
                emit(DataUtil.mapListResult(getBlocksFromRemote, ::mapBlockModel))


                // Then write block to local
                val blockModels = getBlocksFromRemote.getOrNull()?.map { mapBlockModel(it) }

                blockModels?.forEach {
                    localBlockRepository.addBlock(mapBlockModel(it))
                }
            } else {
                // Emit the error
                getBlocksFromRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }


                // Emit blocks from local instead
                logEvent("Getting blocks from local ... ")
                val getBlockFromLocal = localBlockRepository.getAllBlocks()

                if (getBlockFromLocal.isSuccess) {
                    emit(DataUtil.mapListResult(getBlockFromLocal, ::mapBlockModel))
                } else {
                    emit(Result.failure(UnknownError("Data block masih kosong.")))
                }
            }
        }
    }

    override fun addBlock(block: Block): Flow<Result<Boolean>> {
        return flow {
            val blockModel = BlockModel(
                kode = block.kode,
                warna = block.warna
            )

            emitAll(remoteBlockRepository.addNewBlock(blockModel))
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