package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian

interface PengembalianRepository {

    fun getAsFlow(keyId: String, dataMode: DataMode): Flow<Result<Pengembalian?>>

    suspend fun getKeyIds(dataMode: DataMode): Result<List<String>?>

    suspend fun getAll(dataMode: DataMode): Result<List<Pengembalian?>>

    suspend fun insert(pengembalian: Pengembalian): Result<Unit>

    suspend fun update(keyId: String, new: Pengembalian): Result<Unit>

    suspend fun delete(kavling: String)

}