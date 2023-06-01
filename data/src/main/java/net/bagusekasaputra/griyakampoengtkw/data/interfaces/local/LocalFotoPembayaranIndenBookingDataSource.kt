package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel

interface LocalFotoPembayaranIndenBookingDataSource {

    suspend fun get(keyId: String, termin: String): Result<FotoPembayaranIndenBookingModel?>

    suspend fun insert(model: FotoPembayaranIndenBookingModel): Result<Nothing?>

    suspend fun delete(keyId: String, termin: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}