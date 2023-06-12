package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase

class CreateBackupAsyncUseCase(

): AsyncUseCase<CreateBackupAsyncUseCase.Request, CreateBackupAsyncUseCase.Progress>() {

    data class Progress(
        val progress: Int,
        val message: String,
    )
    data class Request(val backupName: String, val backupSavepath: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Progress?>> {
        TODO("Not yet implemented")
    }
}