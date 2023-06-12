package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

class BackupFirebaseBiayaMarketingDataSource: BackupBiayaMarketingDataSource {
    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listBiayaMarketing: List<BiayaMarketingModel>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}