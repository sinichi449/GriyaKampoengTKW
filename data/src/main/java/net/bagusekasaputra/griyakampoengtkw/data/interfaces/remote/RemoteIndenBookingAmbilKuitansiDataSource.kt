package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel

interface RemoteIndenBookingAmbilKuitansiDataSource {

    suspend fun get(keyId: String, termin: String): Result<IndenBookingAmbilKuitansiModel?>

}