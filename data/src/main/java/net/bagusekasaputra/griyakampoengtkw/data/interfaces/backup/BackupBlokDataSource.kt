package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

interface BackupBlokDataSource {

    suspend fun getAllBlocks(): Result<List<BlockModel>?>

    suspend fun createBackup(backupPath: String, listBlok: List<BlockModel>): Result<Nothing?>
}