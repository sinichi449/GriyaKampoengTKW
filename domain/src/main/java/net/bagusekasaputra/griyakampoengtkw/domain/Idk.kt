package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun getFotoPembayaranFolderName() = "fotoPembayaran"

enum class DataMode {
    ONLINE, OFFLINE, DATA_LAMA
}

suspend fun <T> Flow<Result<T>>.firstOrThrow(): T {
    return first().getOrThrow()
}