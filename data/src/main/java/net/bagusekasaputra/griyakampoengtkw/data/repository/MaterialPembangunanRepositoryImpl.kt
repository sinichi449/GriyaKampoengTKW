package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMaterialPembangunanDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class MaterialPembangunanRepositoryImpl(
    private val remoteDataSource: RemoteMaterialPembangunanDataSource,
): MaterialPembangunanRepository {

    override fun getAll(
        untuk: String,
        kategori: MaterialPembangunan.Kategori,
        dataMode: DataMode
    ): Flow<Result<List<MaterialPembangunan>?>> {
        return flow {
            val remoteResult = remoteDataSource.getAll(untuk, kategori.name)
                .onFailure { throw it }

            emit(DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = MyObjectMapper::mapMaterialPembangunan,
            ))
        }
    }

    override suspend fun insert(materialPembangunan: MaterialPembangunan): Result<Unit> {
        return remoteDataSource.insert(
            MyObjectMapper.mapMaterialPembangunan(materialPembangunan)
        )
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