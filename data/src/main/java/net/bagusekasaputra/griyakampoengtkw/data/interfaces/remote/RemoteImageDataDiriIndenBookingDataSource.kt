package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel

interface RemoteImageDataDiriIndenBookingDataSource {

    suspend fun download(model: ImageDataDiriIndenBookingModel): Result<Boolean>

    suspend fun isExist(keyId: String): Result<Boolean>
}