package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class MaterialPembangunanRepositoryImpl: MaterialPembangunanRepository {

    override fun getAll(
        kavling: String,
        kategori: MaterialPembangunan.Kategori,
        dataMode: DataMode
    ): Flow<Result<List<MaterialPembangunan>?>> {
        return flow {
            delay(3000L)

            emit(Result.failure(NotImplementedError("Not yet implemented")))
        }
    }

    override suspend fun insert(
        kavling: String,
        materialPembangunan: MaterialPembangunan
    ): Result<Unit> {
        delay(3000L)

        return Result.failure(NotImplementedError("Not yet implemented"))
    }

    override suspend fun delete(kavling: String, keyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        kavling: String,
        keyId: String,
        newData: MaterialPembangunan
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

}