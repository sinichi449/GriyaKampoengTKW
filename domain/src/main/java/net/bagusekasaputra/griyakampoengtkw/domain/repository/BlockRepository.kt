package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block

interface BlockRepository {

    fun getAllBlocks(offline: Boolean): Flow<Result<List<Block>?>>

    fun addBlock(block: Block): Flow<Result<Nothing?>>

}