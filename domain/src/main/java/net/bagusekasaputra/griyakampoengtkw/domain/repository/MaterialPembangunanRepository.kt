package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan

interface MaterialPembangunanRepository {

    fun getAll(
        untuk: String,
        kategori: MaterialPembangunan.Kategori,
        dataMode: DataMode,
    ): Flow<Result<List<MaterialPembangunan>?>>

    suspend fun insert(materialPembangunan: MaterialPembangunan): Result<Unit>

    suspend fun delete(identifier: MaterialPembangunan.Identifier): Result<Unit>

    suspend fun update(
        identifier: MaterialPembangunan.Identifier,
        newData: MaterialPembangunan
    ): Result<Unit>

}