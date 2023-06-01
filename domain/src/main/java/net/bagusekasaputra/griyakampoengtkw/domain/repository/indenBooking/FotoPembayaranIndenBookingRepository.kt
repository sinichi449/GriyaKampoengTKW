package net.bagusekasaputra.griyakampoengtkw.domain.repository.indenBooking

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking

interface FotoPembayaranIndenBookingRepository {

    suspend fun get(keyId: String, termin: String): Result<FotoPembayaranIndenBooking?>

    suspend fun isExist(keyId: String, termin: String): Result<Boolean>

}