package net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri

import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

interface LocalDataDiriRepository {

    suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?>

    suspend fun addDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): Result<Nothing?>

}