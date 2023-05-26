package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking

interface IndenBookingRepository {

    suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?>

    suspend fun getHargaRumah(keyId: String, dataMode: DataMode): Result<HargaRumahIndenBooking?>

}