package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository

class FotoTambahLuasanRepositoryImpl(
    private val remoteSource: RemoteFotoTambahLuasanDataSource,
): FotoTambahLuasanRepository {

    override fun get(kavling: String, tambahLuasanId: String): Flow<Result<FotoTambahLuasan?>> {
        return flow {
            emit(
                remoteSource.get(kavling, tambahLuasanId).map { model ->
                    if (model != null) MyObjectMapper.mapFotoTambahLuasan(model)
                    else null
                }
            )
        }
    }

}