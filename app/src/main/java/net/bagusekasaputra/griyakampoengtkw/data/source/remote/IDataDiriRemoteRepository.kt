package net.bagusekasaputra.griyakampoengtkw.data.source.remote

import kotlinx.coroutines.flow.Flow

interface IDataDiriRemoteRepository {

    fun getDataDiri(kavlingKode: String): Flow<Result<DataDiriFirebaseModel?>>

}