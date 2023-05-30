package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

interface LocalHargaRumahIndenBookingDataSource {

    suspend fun get(keyId: String): Result<HargaRumahModel?>

    suspend fun insert(keyId: String, model: HargaRumahModel): Result<Nothing?>

    suspend fun update(keyId: String, newModel: HargaRumahModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}