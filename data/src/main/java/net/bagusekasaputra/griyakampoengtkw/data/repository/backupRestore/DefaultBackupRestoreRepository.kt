package net.bagusekasaputra.griyakampoengtkw.data.repository.backupRestore

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository

class DefaultBackupRestoreRepository(

): BackupRestoreRepository {

    override fun getListBackups(): Flow<Result<List<String>?>> {
        TODO("Not yet implemented")
    }

    override fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}