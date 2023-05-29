package net.bagusekasaputra.griyakampoengtkw.data

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

}