package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

interface BackupHargaKavlingDataSource {

    suspend fun getHargaKavling(kavlingKode: String): Result<HargaKavlingModel?>

    suspend fun createBackup(backupPath: String, listHargaKavling: List<HargaKavlingModel>?): Result<Nothing?>

}