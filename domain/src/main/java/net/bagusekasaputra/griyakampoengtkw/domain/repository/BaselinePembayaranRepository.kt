package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran

interface BaselinePembayaranRepository {

    fun get(kavling: String, dataMode: DataMode): Flow<Result<BaselinePembayaran?>>

    fun insert(baselinePembayaran: BaselinePembayaran): Flow<Result<Nothing?>>

    @Deprecated("Will be removed soon.")
    suspend fun getAngsuran(kavling: String, dataMode: DataMode): Long?

    suspend fun refreshCache(kavlings: List<String>): Result<Nothing?>
}