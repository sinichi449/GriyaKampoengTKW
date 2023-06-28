package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class MaterialPembangunanRepositoryImpl: MaterialPembangunanRepository {

    override fun getAll(
        kavling: String,
        dataMode: DataMode
    ): Flow<Result<List<MaterialPembangunan>?>> {
        TODO("Not yet implemented")
    }

}