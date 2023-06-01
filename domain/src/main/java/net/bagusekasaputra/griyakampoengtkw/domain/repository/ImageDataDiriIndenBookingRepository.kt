package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking

interface ImageDataDiriIndenBookingRepository {

    suspend fun get(keyId: String): Result<ImageDataDiriIndenBooking?>

    suspend fun isExist(keyId: String): Result<Boolean>

}