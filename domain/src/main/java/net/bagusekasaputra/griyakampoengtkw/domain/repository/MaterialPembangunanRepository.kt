package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan

interface MaterialPembangunanRepository {

    fun getAll(kavling: String, dataMode: DataMode): Flow<Result<List<MaterialPembangunan>?>>

}