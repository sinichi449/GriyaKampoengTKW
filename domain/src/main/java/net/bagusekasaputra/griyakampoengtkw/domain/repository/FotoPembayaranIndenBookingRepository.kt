package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking

interface FotoPembayaranIndenBookingRepository {

    suspend fun get(keyId: String, termin: String): Result<FotoPembayaranIndenBooking?>

    suspend fun insert(fotoPembayaran: FotoPembayaranIndenBooking): Result<Nothing?>

    suspend fun delete(keyId: String, termin: String): Result<Nothing?>

    suspend fun isExist(keyId: String, termin: String): Result<Boolean>

}