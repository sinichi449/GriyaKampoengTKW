package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository

class GetListBackupAsyncUseCase(
    private val backupRestoreRepository: BackupRestoreRepository,
): AsyncUseCase<GetListBackupAsyncUseCase.Request, List<String>>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<String>?>> {
        return backupRestoreRepository.getListBackups()
    }
}