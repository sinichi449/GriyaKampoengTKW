package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling

interface FeeMarketingRepository: BatchableWithKavling<FeeMarketing?> {

    fun getByKavlingKode(kavlingKode: String, dataMode: DataMode): Flow<Result<FeeMarketing?>>

    // I need to get a strictly from online/remote data source because the normal get method
    // will return the data from local if an error occurred.
    fun getAllOnline(kavlingKode: String): Flow<Result<FeeMarketing?>>

    fun addFeeMarketing(feeMarketing: FeeMarketing): Flow<Result<Nothing?>>

    fun updateFeeMarketing(oldFeeMarketing: FeeMarketing, newFeeMarketing: FeeMarketing): Flow<Result<Nothing?>>

    fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>>

    /**
     * Batch Operations
     */
    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, FeeMarketing?>?>>

    override fun fromBackupBatch(backupName: String, listKavling: List<String>): Flow<Result<Map<String, FeeMarketing?>?>>

    fun getBatchOffline(kavlingList: List<String>): Flow<Result<List<FeeMarketing>?>>

    fun getBatchBackup(kavlingList: List<String>): Flow<Result<Map<String, FeeMarketing?>?>>
}