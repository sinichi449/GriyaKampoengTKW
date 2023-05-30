package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface LocalDataDiriDataSource {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    suspend fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Result<Nothing?>

    suspend fun deleteDataDiri(kavlingKode: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<DataDiriModel?>

    suspend fun insertFromIndenBooking(keyId: String, model: DataDiriModel): Result<Nothing?>

    suspend fun updateFromIndenBooking(keyId: String, newModel: DataDiriModel): Result<Nothing?>

    suspend fun deleteAllFromIndenBooking(): Result<Nothing?>
}