package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

class BackupFirebaseBlokDataSource: BackupBlokDataSource {
    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listBlok: List<BlockModel>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}