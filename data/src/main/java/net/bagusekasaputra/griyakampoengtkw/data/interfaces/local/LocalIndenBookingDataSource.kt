package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

interface LocalIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

}