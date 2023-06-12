package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

class BackupFirebaseHargaKavlingDataSource: BackupHargaKavlingDataSource {
    override suspend fun getHargaKavling(kavlingKode: String): Result<HargaKavlingModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listHargaKavling: List<HargaKavlingModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}