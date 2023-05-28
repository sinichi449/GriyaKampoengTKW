package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository

class AmbilKuitansiRepositoryImpl(
    private val remoteDataSource: RemoteAmbilKuitansiDataSource,
    private val cacheHelper: CacheHelper,
): AmbilKuitansiRepository {

    override suspend fun get(kavling: String, termin: String): Result<AmbilKuitansi?> {
        val remoteResult = remoteDataSource.get(kavling, termin)

        return DataUtil.mapSingleResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapAmbilKuitansi
        )
    }

}