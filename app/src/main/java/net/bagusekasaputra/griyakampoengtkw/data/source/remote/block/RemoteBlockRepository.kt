package net.bagusekasaputra.griyakampoengtkw.data.source.remote.block

import kotlinx.coroutines.flow.Flow

interface RemoteBlockRepository {

    fun getAllBlocks(): Flow<List<BlockModel>?>

    fun addNewBlock(blockModel: BlockModel): Flow<Boolean>

}