package net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface RemoteDataDiriRepository {

    fun getDataDiri(kavlingKode: String): Flow<Result<DataDiriModel?>>

    fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>
}