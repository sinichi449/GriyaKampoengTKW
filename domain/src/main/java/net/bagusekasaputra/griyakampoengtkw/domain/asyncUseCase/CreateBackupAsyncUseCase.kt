package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class CreateBackupAsyncUseCase(
    private val blokRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
): AsyncUseCase<CreateBackupAsyncUseCase.Request, String>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<String?>> {
        return flow {
            blokRepository.createBackup().first()
                .onSuccess {
                    emit(Result.success("Bloks ok!"))
                }
                .onFailure {
                    emit(Result.failure(it))
                }


            kavlingRepository.createBackup().first()
                .onSuccess {
                    emit(Result.success("Kavling ok!"))

                    delay(2500L)

                    emit(Result.success("Completed"))
                }
                .onFailure {
                    emit(Result.failure(it))
                }
        }
    }
}