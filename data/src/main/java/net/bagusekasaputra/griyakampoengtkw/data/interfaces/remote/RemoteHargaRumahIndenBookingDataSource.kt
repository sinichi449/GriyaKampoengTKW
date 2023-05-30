package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

interface RemoteHargaRumahIndenBookingDataSource {

    suspend fun get(keyId: String): Result<HargaRumahModel?>

    suspend fun update(keyId: String, newModel: HargaRumahModel): Result<Nothing?>

}