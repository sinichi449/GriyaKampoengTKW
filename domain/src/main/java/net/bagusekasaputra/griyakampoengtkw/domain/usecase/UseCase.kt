package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.misc.Result
import net.bagusekasaputra.griyakampoengtkw.domain.entity.misc.UseCaseException

abstract class UseCase<I: UseCase.Request, O: UseCase.Response> {

    interface Request

    interface Response

    protected abstract fun process(request: I): Flow<O>

    fun execute(request: I) = process(request)
        .map {
            Result.Success(it)
        }
        .flowOn(Dispatchers.IO)
        .catch {
            Result.Error(UseCaseException.createFromThrowable(it))
        }
}