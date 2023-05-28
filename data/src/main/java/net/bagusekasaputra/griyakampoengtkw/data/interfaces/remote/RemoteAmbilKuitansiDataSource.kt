package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.AmbilKuitansiModel

interface RemoteAmbilKuitansiDataSource {

    suspend fun get(kavling: String, termin: String): Result<AmbilKuitansiModel?>

}