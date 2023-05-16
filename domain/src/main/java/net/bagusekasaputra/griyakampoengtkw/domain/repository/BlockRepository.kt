package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block

interface BlockRepository {

    fun getAllBlocks(dataMode: DataMode): Flow<Result<List<Block>?>>

    fun addBlock(block: Block): Flow<Result<Nothing?>>

    suspend fun refreshCache(): Result<Nothing?>

}