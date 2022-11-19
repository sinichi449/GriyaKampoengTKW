package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface LocalDataDiriDataSource {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    suspend fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Result<Nothing?>

}