package net.bagusekasaputra.griyakampoengtkw.data.backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupRestoreDataSource
import net.lingala.zip4j.ZipFile
import java.io.File

class BackupRestoreDataSourceImpl: BackupRestoreDataSource {

    override suspend fun createZippedBackup(
        backupPath: String,
        backupName: String,
        savePath: String
    ): Result<Nothing?> {
        return try {
            val backupPathFile = File(backupPath)
            val listBackupFiles = mutableListOf<File>()
            val listBackupFolders = mutableListOf<File>()

            backupPathFile.listFiles()?.forEach {
                if (it.isDirectory) {
                    listBackupFolders.add(it)
                } else {
                    listBackupFiles.add(it)
                }
            }

            if (listBackupFiles.isEmpty() && listBackupFolders.isEmpty()) {
                throw Exception("ERROR: File pada backupPath NULL atau kosong!")
            } else {
                val zipFile = ZipFile("$savePath/${backupName}.zip")

                zipFile.addFiles(listBackupFiles)
                listBackupFolders.forEach {
                    zipFile.addFolder(it)
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}