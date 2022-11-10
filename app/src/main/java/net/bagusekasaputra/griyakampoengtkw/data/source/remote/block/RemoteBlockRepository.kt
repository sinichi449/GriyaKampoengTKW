package net.bagusekasaputra.griyakampoengtkw.data.source.remote.block

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface RemoteBlockRepository {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

    fun addNewBlock(blockModel: BlockModel): Flow<Result<Boolean>>
}