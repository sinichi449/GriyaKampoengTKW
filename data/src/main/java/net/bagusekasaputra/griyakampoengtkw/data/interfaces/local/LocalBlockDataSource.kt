package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface LocalBlockDataSource {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

    suspend fun addBlock(blockModel: BlockModel): Result<Nothing?>

}