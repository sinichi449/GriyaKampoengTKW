package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel

class BackupFirebaseCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource {
    override suspend fun getCatatanPembayaran(kavlingKode: String): Result<KavlingCatatanPembayaranModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listCatatanPembayaran: List<KavlingCatatanPembayaranModel>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}