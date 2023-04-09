package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class CreateBackupAsyncUseCase(
    private val blokRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
): AsyncUseCase<CreateBackupAsyncUseCase.Request, CreateBackupAsyncUseCase.Progress>() {

    data class Progress(
        val progress: Int,
        val message: String,
    )
    data class Request(val backupName: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Progress?>> {
        return flow {
            // TODO
            var progress = 0

            while (progress <= 100) {
                emit(Result.success(Progress(progress, "$progress% selesai ...")))

                delay(2000L)

                progress += 10
            }
        }
    }
}