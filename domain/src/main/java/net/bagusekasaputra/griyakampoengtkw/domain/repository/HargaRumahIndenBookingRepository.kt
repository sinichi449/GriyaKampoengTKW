package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking

interface HargaRumahIndenBookingRepository {

    suspend fun get(keyId: String, dataMode: DataMode): Result<HargaRumahIndenBooking?>

}