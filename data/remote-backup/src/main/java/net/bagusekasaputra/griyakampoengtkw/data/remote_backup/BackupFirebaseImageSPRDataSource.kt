package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageSPRDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

class BackupFirebaseImageSPRDataSource: BackupImageSPRDataSource {
    override suspend fun getImageSPR(kavlingKode: String): Result<ImageSprModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listSprImage: List<ImageSprModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}