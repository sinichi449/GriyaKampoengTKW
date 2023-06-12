package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable

interface RemoteBackupRestoreDataSource: Cacheable {

    suspend fun getListBackup(): Result<List<String>?>

}