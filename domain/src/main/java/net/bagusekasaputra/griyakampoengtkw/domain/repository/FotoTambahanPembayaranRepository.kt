package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahanPembayaran

interface FotoTambahanPembayaranRepository {

    suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaran?>

    suspend fun insert(entity: FotoTambahanPembayaran): Result<Nothing?>

    suspend fun isFotoExists(kavling: String, id: String): Result<Boolean>
}