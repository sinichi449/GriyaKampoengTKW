package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface BackupDataDiriDataSource {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    suspend fun createBackup(backupPath: String, listDataDiri: Map<String, DataDiriModel?>): Result<Nothing?>

}