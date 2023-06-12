package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

class BackupFirebaseFotoPembayaranDataSource: BackupFotoPembayaranDataSource {
    override suspend fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Result<FotoPembayaranModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun isFotoPembayaranExist(
        kavlingKode: String,
        termin: String
    ): Result<Boolean?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listFotoPembayaran: List<FotoPembayaranModel>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}