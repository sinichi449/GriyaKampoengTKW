package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

interface PembayaranRepository {

    fun getPembayaran(kavlingKode: String): Flow<Result<Pembayaran>>

    fun addPembayaran(kavlingKode: String, pembayaran: Pembayaran): Flow<Result<Boolean>>

    fun updatePembayaran(kavlingKode: String, oldPembayaran: Pembayaran, newPembayaran: Pembayaran): Flow<Result<Boolean>>

    fun getLatestTotalUangMasuk(kavlingKode: String): Flow<Result<Long>>
}