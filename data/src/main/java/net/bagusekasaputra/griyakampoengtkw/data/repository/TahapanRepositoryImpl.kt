package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTahapanDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TahapanRepository

class TahapanRepositoryImpl(
    private val remoteTahapanDataSource: RemoteTahapanDataSource,
): TahapanRepository {

    override suspend fun getAllTahapan(): Result<List<Tahapan>?> {
        return DataUtil.mapListResult(
            originResult = remoteTahapanDataSource.getAll(),
            targetMapper = MyObjectMapper::mapTahapan,
        )
    }
}