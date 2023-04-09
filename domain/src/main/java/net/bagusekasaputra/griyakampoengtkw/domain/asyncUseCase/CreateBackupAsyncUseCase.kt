package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository

class CreateBackupAsyncUseCase(
    private val blokRepository: BlockRepository,
    private val backupRestoreRepository: BackupRestoreRepository,
): AsyncUseCase<CreateBackupAsyncUseCase.Request, CreateBackupAsyncUseCase.Progress>() {

    data class Progress(
        val progress: Int,
        val message: String,
    )
    data class Request(val backupName: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Progress?>> {
        return callbackFlow {
            trySendBlocking(Result.success(Progress(50, "Mendownload Blok ...")))

            val listBlok = blokRepository.getAllBlocks(dataMode = DataMode.ONLINE)
                .first().getOrThrow() ?: emptyList()

            val backupRestoreEntity = BackupRestoreEntity(
                backupName = request.backupName,
                listBlok = listBlok,
            )

            backupRestoreRepository.createBackup(backupRestoreEntity)
                .first()
                .onSuccess {
                    trySendBlocking(Result.success(Progress(100, "Completed")))
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }
}