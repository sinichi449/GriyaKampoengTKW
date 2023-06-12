package net.bagusekasaputra.griyakampoengtkw.domain.repository

interface BackupRestoreRepository {

    suspend fun getListBackups(): Result<List<String>?>

}