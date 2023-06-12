package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

class BackupFirebasePembayaranDataSource: BackupPembayaranDataSource {
    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listPembayaran: Map<String, List<PembayaranModel>?>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}