package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel

interface BackupFeeMarketingDataSource {

    suspend fun getFeeMarketing(kavlingKode: String): Result<FeeMarketingModel?>

    suspend fun createBackup(backupPath: String, listFeeMarketing: List<FeeMarketingModel>?): Result<Nothing?>

}