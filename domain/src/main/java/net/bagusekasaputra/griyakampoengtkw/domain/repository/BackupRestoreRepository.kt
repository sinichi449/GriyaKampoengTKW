package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity

interface BackupRestoreRepository {

    fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>>

}