package net.bagusekasaputra.griyakampoengtkw.data.source.local.block

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface LocalBlockDataSource {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

    suspend fun addBlock(blockModel: BlockModel): Result<Nothing?>

}