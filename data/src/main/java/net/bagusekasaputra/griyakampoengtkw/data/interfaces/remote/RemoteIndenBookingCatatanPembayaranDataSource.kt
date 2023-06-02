package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel

interface RemoteIndenBookingCatatanPembayaranDataSource {

    suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaranModel?>

}