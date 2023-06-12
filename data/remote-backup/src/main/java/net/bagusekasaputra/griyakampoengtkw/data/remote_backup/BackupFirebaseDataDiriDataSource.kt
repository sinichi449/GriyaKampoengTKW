package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

class BackupFirebaseDataDiriDataSource: BackupDataDiriDataSource {
    override suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listDataDiri: Map<String, DataDiriModel?>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}