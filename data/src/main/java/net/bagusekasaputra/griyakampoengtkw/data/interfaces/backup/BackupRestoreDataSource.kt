package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

interface BackupRestoreDataSource {

    suspend fun createZippedBackup(backupPath: String, backupName: String, savePath: String): Result<Nothing?>

}