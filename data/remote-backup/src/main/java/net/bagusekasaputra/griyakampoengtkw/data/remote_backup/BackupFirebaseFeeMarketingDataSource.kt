package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel

class BackupFirebaseFeeMarketingDataSource: BackupFeeMarketingDataSource {
    override suspend fun getFeeMarketing(kavlingKode: String): Result<FeeMarketingModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listFeeMarketing: List<FeeMarketingModel>?
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}