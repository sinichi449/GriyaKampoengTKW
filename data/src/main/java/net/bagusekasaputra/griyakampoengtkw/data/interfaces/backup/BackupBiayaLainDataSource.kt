package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

interface BackupBiayaLainDataSource {

    suspend fun getAllBiayaLain(): Result<List<BiayaLainModel>?>

    suspend fun createBackup(backupPath: String, listBiayaLain: List<BiayaLainModel>?): Result<Nothing?>

}