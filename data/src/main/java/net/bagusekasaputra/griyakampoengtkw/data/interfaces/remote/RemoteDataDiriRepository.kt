package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface RemoteDataDiriRepository {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>
}