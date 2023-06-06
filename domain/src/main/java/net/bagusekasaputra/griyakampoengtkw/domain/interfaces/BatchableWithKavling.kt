package net.bagusekasaputra.griyakampoengtkw.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface BatchableWithKavling<T> {

    fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, T>?>>

    fun backupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, T>?>>

}