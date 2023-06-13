package net.bagusekasaputra.griyakampoengtkw.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.UUID

object DataUtil {

    fun <O, T> mapSingleResult(
        originResult: Result<O?>,
        targetMapper: (O) -> T,
    ): Result<T?> {
        return originResult.map {
            it?.let(targetMapper)
        }
    }

    fun <O, T> mapListResult(
        originResult: Result<List<O>?>,
        targetMapper: (O) -> T,
    ): Result<List<T>?> {
        return originResult.map { list ->
            list?.map(targetMapper)
        }
    }

    fun generateKeyId(): String {
        return UUID.randomUUID().toString()
    }
    fun <O> networkBoundResources(
        shouldFetch: suspend () -> Boolean,
        query: suspend () -> Result<O>,
        fetch: suspend () -> Result<O>,
        saveFetchResult: suspend (remoteModel: O) -> Result<Unit>,
    ): Flow<Result<O>> {
        return flow {
            if (shouldFetch()) {
                val fetchResult = fetch().getOrThrow()

                fetchResult?.also { saveFetchResult(it) }
            }

            // Even when fetch, it should get the data from local data source.
            val queryResult = query().getOrThrow()

            emit(Result.success(queryResult))
        }.catch {
            it.printStackTrace()

            emit(Result.failure(it))
        }
    }
}