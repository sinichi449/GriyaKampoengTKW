package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

// The name of this class not special. It just the designation to put a difference
// with the old UseCase. I want to migrate all the old UseCase, to this one.
abstract class AsyncUseCase<I: AsyncUseCase.Request, O> {

    interface Request

    protected abstract fun process(request: I): Flow<Result<O?>>

    fun execute(request: I): Flow<Result<O?>> {
        return process(request)
            .flowOn(Dispatchers.IO)
            .catch {
                emit(Result.failure(it))
            }
    }

}