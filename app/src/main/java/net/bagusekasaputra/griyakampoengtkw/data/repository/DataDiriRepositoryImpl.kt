package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.IDataDiriRemoteRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDiriRepositoryImpl @Inject constructor(
    private val iDataDiriRemoteRepository: IDataDiriRemoteRepository
): DataDiriRepository {

    override fun getDataDiri(kavlingKode: String): Flow<DataDiri?> {
        // TODO
        return flow {

        }
    }
}