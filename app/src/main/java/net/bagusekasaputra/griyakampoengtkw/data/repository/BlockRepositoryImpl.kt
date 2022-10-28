package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.LocalBlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRepositoryImpl @Inject constructor(
    private val localBlockRepository: LocalBlockRepository
): BlockRepository {

    override fun getAllBlocks(): Flow<List<Block>> {
        return flow {
            val blocks = localBlockRepository.getAllBlockModels().map {
                mapModel(it)
            }
            emit(blocks)
        }
    }

    private fun mapModel(blockModel: BlockModel): Block {
        return Block(
            kode = blockModel.kode,
            warna = blockModel.warna
        )
    }

}