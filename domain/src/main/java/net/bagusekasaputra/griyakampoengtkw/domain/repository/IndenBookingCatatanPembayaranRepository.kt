package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.IndenBookingCatatanPembayaran

interface IndenBookingCatatanPembayaranRepository {

    suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaran?>

    suspend fun insert(catatanPembayaran: IndenBookingCatatanPembayaran): Result<Nothing?>

}