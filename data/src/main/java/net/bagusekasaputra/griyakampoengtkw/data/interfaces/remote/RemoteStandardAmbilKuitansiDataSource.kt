package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.StandardAmbilKuitansiModel

interface RemoteStandardAmbilKuitansiDataSource {

    suspend fun get(kavling: String, termin: String): Result<StandardAmbilKuitansiModel?>

    suspend fun update(model: StandardAmbilKuitansiModel): Result<Nothing?>

    suspend fun delete(kavling: String, termin: String): Result<Nothing?>

}