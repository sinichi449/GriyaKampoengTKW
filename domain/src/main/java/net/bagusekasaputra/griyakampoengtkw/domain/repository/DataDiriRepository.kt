package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

interface DataDiriRepository {

    fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getBatchFromRemoteBackup(backupName: String, listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getDataDiri(kavlingKode: String, dataMode: DataMode): Flow<Result<DataDiri?>>

    fun getFromRemoteBackup(backupName: String, kavlingKode: String): Flow<Result<DataDiri?>>

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>

    suspend fun refreshCache(kavlings: List<Kavling>): Result<Nothing?>


    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<DataDiri?>

    // This will return the the key id
    suspend fun insertFromIndenBooking(dataDiri: DataDiri): Result<String?>

    suspend fun updateFromIndenBooking(keyId: String, newDataDiri: DataDiri): Result<Nothing?>
}