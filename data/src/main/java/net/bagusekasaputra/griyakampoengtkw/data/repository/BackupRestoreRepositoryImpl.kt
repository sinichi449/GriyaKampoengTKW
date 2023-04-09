package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository
import java.io.File

class BackupRestoreRepositoryImpl(
    private val internalFiles: File,
    private val backupBlokDataSource: BackupBlokDataSource,
): BackupRestoreRepository {

    override fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>> {
        return callbackFlow {
            try {
                val backupPath = File(internalFiles, "data_lama/${backupRestoreEntity.backupName}")
                if (backupPath.exists().not()) backupPath.mkdirs()


                val listBlok = backupRestoreEntity.listBlok.map {
                    BlockRepositoryImpl.mapBlockModel(it)
                }
                backupBlokDataSource.createBackup(backupPath.absolutePath, listBlok).onFailure {
                    trySendBlocking(Result.failure(it))
                }

                trySendBlocking(Result.success(null))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}