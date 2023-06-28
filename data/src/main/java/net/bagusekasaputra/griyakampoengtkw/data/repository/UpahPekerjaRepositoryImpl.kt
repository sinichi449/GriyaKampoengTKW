package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.domain.repository.UpahPekerjaRepository

class UpahPekerjaRepositoryImpl: UpahPekerjaRepository {

    override fun getAll(kavling: String, dataMode: DataMode): Flow<Result<List<UpahPekerja>?>> {
        TODO("Not yet implemented")
    }

}