package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

class BackupFirebaseBiayaLainDataSource: BackupBiayaLainDataSource {
    override suspend fun getAllBiayaLain(): Result<List<BiayaLainModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listBiayaLain: List<BiayaLainModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}