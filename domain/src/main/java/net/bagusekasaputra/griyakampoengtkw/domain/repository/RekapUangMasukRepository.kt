package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapUangMasuk

interface RekapUangMasukRepository {

    fun getAll(): Flow<Result<List<RekapUangMasuk>?>>

    fun insert(rekapUangMasuk: RekapUangMasuk): Flow<Result<Nothing?>>

    fun clearAll(): Flow<Result<Nothing?>>

}