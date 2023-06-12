package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface BackupBlokDataSource {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

}