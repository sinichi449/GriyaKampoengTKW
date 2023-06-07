package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

fun getFotoPembayaranFolderName() = "fotoPembayaran"

enum class DataMode {
    ONLINE, OFFLINE, DATA_LAMA
}

suspend fun <T> Flow<Result<T>>.firstOrThrow(): T {
    return first().getOrThrow()
}

fun Double.juta(): Long {
    val satuJuta = BigDecimal(1_000_000L)

    return BigDecimal(this)
        .multiply(satuJuta)
        .toLong()
}