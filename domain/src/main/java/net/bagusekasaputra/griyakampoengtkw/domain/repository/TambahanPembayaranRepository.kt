package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran

interface TambahanPembayaranRepository {

    suspend fun getAllByKavling(kavling: String): Result<List<TambahanPembayaran>?>

    suspend fun getById(kavling: String, id: String): Result<TambahanPembayaran?>

    suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?>

    suspend fun update(kavling: String, id: String, newData: TambahanPembayaran): Result<Nothing?>
}