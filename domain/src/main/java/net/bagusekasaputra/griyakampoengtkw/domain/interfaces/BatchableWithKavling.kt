package net.bagusekasaputra.griyakampoengtkw.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface BatchableWithKavling<T> {

    fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, T>?>>

    fun fromBackupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, T>?>>

    companion object {
        const val INDEX_BATCHABLE_DATA_DIRI = 0
        const val INDEX_BATCHABLE_HARGA_KAVLING = 1
        const val INDEX_BATCHABLE_PEMBAYARAN = 2
        const val INDEX_BATCHABLE_FEE_MARKETING = 3
        const val INDEX_BATCHABLE_BIAYA_MARKETING = 4
    }

}