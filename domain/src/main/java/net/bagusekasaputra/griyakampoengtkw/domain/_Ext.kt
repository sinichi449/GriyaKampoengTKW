package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

fun getFotoPembayaranFolderName() = "fotoPembayaran"

enum class DataMode {
    ONLINE, OFFLINE, DATA_LAMA
}

suspend fun <T> Flow<Result<T>>.firstOrThrow(): T {
    return first().getOrThrow()
}

//suspend fun <T> Flow<Result<T>>.firstOrEmitFailure(collector: FlowCollector<Result<T>>): T? {
//    return first()
//        .onFailure { collector.emit(Result.failure(it)) }
//        .getOrNull()
//}
//
suspend fun <T, R> Result<T>.getOrEmitFailure(collector: FlowCollector<Result<R>>): T? {
    return onFailure { collector.emit(Result.failure(it)) }
        .getOrNull()
}

fun <T> MutableList<T>.addIfNotNull(element: T?) {
    element?.let { this.add(it) }
}

fun Double.juta(): Long {
    val satuJuta = BigDecimal(1_000_000L)

    return BigDecimal(this)
        .multiply(satuJuta)
        .toLong()
}