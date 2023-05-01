package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

interface BackupBiayaMarketingDataSource {

    suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?>

    suspend fun createBackup(backupPath: String, listBiayaMarketing: List<BiayaMarketingModel>): Result<Nothing?>

}