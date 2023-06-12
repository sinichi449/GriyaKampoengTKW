package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupRestoreDataSource

class BackupFirebaseBackupRestoreDataSource: BackupRestoreDataSource {
    override suspend fun createZippedBackup(
        backupPath: String,
        backupName: String,
        savePath: String
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}