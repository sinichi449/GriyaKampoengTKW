package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

class BackupFirebaseKavlingDataSource: BackupKavlingDataSource {
    override suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listKavling: HashMap<String, List<KavlingModel>>
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}