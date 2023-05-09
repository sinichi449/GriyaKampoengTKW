package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

interface RemoteBackupRestoreDataSource {

    suspend fun getListBackup(): Result<List<String>?>

}