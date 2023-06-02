package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.IndenBookingCatatanPembayaran

interface IndenBookingCatatanPembayaranRepository {

    suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaran?>

    suspend fun insert(catatanPembayaran: IndenBookingCatatanPembayaran): Result<Nothing?>

    suspend fun update(keyId: String, newCatatanPembayaran: IndenBookingCatatanPembayaran): Result<Nothing?>

    suspend fun delete(keyId: String): Result<Nothing?>
}