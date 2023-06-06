package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling

interface DataDiriRepository: BatchableWithKavling<DataDiri?> {

    fun getDataDiri(kavlingKode: String, dataMode: DataMode): Flow<Result<DataDiri?>>

    fun getFromRemoteBackup(backupName: String, kavlingKode: String): Flow<Result<DataDiri?>>

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>

    suspend fun refreshCache(kavlings: List<String>): Result<Nothing?>

    /**
     * Batch Operations
     */
    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    override fun backupBatch(backupName: String, listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<DataDiri?>

    // This will return the the key id
    suspend fun insertFromIndenBooking(dataDiri: DataDiri): Result<String?>

    suspend fun updateFromIndenBooking(keyId: String, newDataDiri: DataDiri): Result<Nothing?>
}