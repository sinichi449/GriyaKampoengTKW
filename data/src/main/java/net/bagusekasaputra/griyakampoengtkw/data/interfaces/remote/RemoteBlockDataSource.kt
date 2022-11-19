package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface RemoteBlockDataSource {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

    suspend fun addNewBlock(blockModel: BlockModel): Result<Nothing?>
}