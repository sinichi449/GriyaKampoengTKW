package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.BlockModel
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
                        warna = parseBlockWarna(it.warna?: "Black")
                    )
                }
            }

            emitAll(flowBlocks)
        }
    }

    override fun addBlock(block: Block): Flow<Boolean> {
        return flow {
            val blockModel = BlockModel(
                kode = block.kode,
                warna = parseBlockWarna(block.warna?: R.color.black)
            )

            emitAll(remoteBlockRepository.addNewBlock(blockModel))
        }
    }

    companion object {
        fun parseBlockWarna(warna: String): Int? {
            return when (warna) {
                "Red" -> R.color.abang
                "Orange" -> R.color.oren_1
                "Yellow" -> R.color.oren_2
                "Black" -> R.color.black
                else -> null
            }
        }

        fun parseBlockWarna(warna: Int): String {
            return when (warna) {
                R.color.abang -> "Red"
                R.color.oren_1 -> "Orange"
                R.color.oren_2 -> "Yellow"
                else -> "Black"
            }
        }
    }
}