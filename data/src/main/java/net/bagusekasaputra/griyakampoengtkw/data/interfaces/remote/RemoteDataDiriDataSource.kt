package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface RemoteDataDiriDataSource {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    suspend fun getFromBackup(backupName: String, kavling: String): Result<DataDiriModel?>

    fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>


    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<DataDiriModel?>

}