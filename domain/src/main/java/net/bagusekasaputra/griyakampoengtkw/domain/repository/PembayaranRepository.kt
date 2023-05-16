package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

interface PembayaranRepository {

    fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>

    fun getBatchFromRemoteBackup(backupName: String, listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>

    fun getAllPembayaran(kavlingKode: String, dataMode: DataMode): Flow<Result<List<Pembayaran>?>>

    // I need to get a strictly from online/remote data source because the normal get method
    // will return the data from local if an error occurred.
    fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>>

    fun addPembayaran(kavlingKode: String, hargaKavling: Long, pembayaran: Pembayaran): Flow<Result<Boolean>>

    fun updatePembayaran(kavlingKode: String, oldPembayaran: Pembayaran, newPembayaran: Pembayaran): Flow<Result<Boolean>>

    fun deletePembayaranByTermin(kavlingKode: String, termin: String): Flow<Result<Boolean>>

    fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>>

    suspend fun sudahBayarAngsuran(kavlingKode: String, bulan: Int, dataMode: DataMode): Result<Boolean?>

    // Currently offline only
    suspend fun getUangMasukBulanIni(kavlingKode: String, dataMode: DataMode): Long?

    suspend fun refreshCache(kavlings: List<String>): Result<Nothing?>
}