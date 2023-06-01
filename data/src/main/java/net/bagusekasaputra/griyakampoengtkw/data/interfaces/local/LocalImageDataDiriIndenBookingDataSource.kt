package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel

interface LocalImageDataDiriIndenBookingDataSource {

    suspend fun get(keyId: String): Result<ImageDataDiriIndenBookingModel?>

    suspend fun insert(model: ImageDataDiriIndenBookingModel): Result<Nothing?>

    suspend fun delete(model: ImageDataDiriIndenBookingModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}