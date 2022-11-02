package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block

interface BlockRepository {

    fun getAllBlocks(): Flow<List<Block>?>

    fun addBlock(block: Block): Flow<Boolean>

}