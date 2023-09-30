package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran

interface TambahanPembayaranRepository {

    suspend fun getAllByKavling(kavling: String): Result<List<TambahanPembayaran>?>

    suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?>

}