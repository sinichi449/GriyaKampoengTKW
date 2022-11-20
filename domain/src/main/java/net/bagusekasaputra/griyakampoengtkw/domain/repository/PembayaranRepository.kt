package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

interface PembayaranRepository {

    fun getAllPembayaran(kavlingKode: String, offline: Boolean): Flow<Result<List<Pembayaran>?>>

    fun addPembayaran(kavlingKode: String, hargaKavling: Long, pembayaran: Pembayaran): Flow<Result<Boolean>>

    fun updatePembayaran(kavlingKode: String, oldPembayaran: Pembayaran, newPembayaran: Pembayaran): Flow<Result<Boolean>>

    fun deletePembayaranByTermin(kavlingKode: String, termin: String): Flow<Result<Boolean>>

    fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>>
}