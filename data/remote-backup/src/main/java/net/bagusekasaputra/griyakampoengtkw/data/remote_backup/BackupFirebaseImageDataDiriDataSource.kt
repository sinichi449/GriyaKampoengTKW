package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

class BackupFirebaseImageDataDiriDataSource: BackupImageDataDiriDataSource {
    override suspend fun getImageDataDiri(kavlingKode: String): Result<ImageDataDiriModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listImageDataDiri: List<ImageDataDiriModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}