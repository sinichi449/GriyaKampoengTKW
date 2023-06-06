package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling

interface BiayaMarketingRepository: BatchableWithKavling<List<BiayaMarketing>?> {

    fun getAllByKavlingKode(kavlingKode: String, dataMode: DataMode): Flow<Result<List<BiayaMarketing>?>>

    // I need to get a strictly from online/remote data source because the normal get method
    // will return the data from local if an error occurred.
    fun getAllOnline(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>>

    fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>>

    fun update(oldBiayaMarketing: BiayaMarketing, newBiayaMarketing: BiayaMarketing): Flow<Result<Nothing?>>

    fun deleteSingle(kavlingKode: String, biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>>

    fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>>

    /**
     * Batch Operations
     */
    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>?>>

    override fun fromBackupBatch(backupName: String, listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>?>>

    fun getBatchOffline(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>>>

}