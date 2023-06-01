package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel

interface RemoteFotoPembayaranIndenBookingDataSource {

    // Returns true if available, and false if not available
    suspend fun download(model: FotoPembayaranIndenBookingModel): Result<Boolean>

    suspend fun isExist(keyId: String, termin: String): Result<Boolean>
}