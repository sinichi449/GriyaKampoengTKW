package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel

interface RemoteIndenBookingCatatanPembayaranDataSource {

    suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaranModel?>

    suspend fun insert(model: IndenBookingCatatanPembayaranModel): Result<Nothing?>

    suspend fun update(keyId: String, newModel: IndenBookingCatatanPembayaranModel): Result<Nothing?>

    suspend fun delete(keyId: String): Result<Nothing?>

}