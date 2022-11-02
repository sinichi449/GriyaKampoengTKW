package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.data.source.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.RemoteBlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRepositoryImpl @Inject constructor(
    private val remoteBlockRepository: RemoteBlockRepository
): BlockRepository {

    override fun getAllBlocks(): Flow<List<Block>?> {
        return flow {
            val flowBlocks = remoteBlockRepository.getAllBlocks().map { blockModels ->
                blockModels?.map {
                    Block(
                        kode = it.kode,
                        warna = it.warna
                    )
                }
            }

            emitAll(flowBlocks)
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
}