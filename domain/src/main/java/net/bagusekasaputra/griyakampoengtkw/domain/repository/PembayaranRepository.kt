package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling

interface PembayaranRepository: BatchableWithKavling<List<Pembayaran>?> {

    suspend fun getByKavlingAndTermin(kavlingKode: String, termin: String): Result<Pembayaran?>

    fun getAllPembayaran(kavlingKode: String, dataMode: DataMode): Flow<Result<List<Pembayaran>?>>

    // I need to get a strictly from online/remote data source because the normal get method
    // will return the data from local if an error occurred.
    fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>>

    fun addPembayaran(kavlingKode: String, pembayaran: Pembayaran): Flow<Result<Boolean>>

    suspend fun updatePembayaran(kavlingKode: String, newPembayaran: Pembayaran): Result<Nothing?>

    fun deletePembayaranByTermin(kavlingKode: String, termin: String): Flow<Result<Boolean>>

    fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>>

    @Deprecated("Will be removed soon.")
    suspend fun sudahBayarAngsuran(
        kavlingKode: String,
        bulan: Int,
        tahun: Int,
        dataMode: DataMode
    ): Result<Boolean?>

    suspend fun refreshCache(kavlings: List<String>): Result<Nothing?>

    /**
     * Batch Operation
     */
    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>

    override fun fromBackupBatch(backupName: String, listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>>


    /**
     * Inden Booking related
     */
    suspend fun getAllFromIndenBooking(keyId: String): Result<List<Pembayaran>?>

    suspend fun insertFromIndenBooking(keyId: String, pembayaran: Pembayaran): Result<Nothing?>
}