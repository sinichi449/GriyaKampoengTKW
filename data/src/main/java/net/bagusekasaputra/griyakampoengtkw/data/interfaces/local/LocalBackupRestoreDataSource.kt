package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable

interface LocalBackupRestoreDataSource: Cacheable {

    suspend fun getListBackups(): Result<List<String>?>

    suspend fun insertAll(items: List<String>): Result<Unit>

    suspend fun invalidate(): Result<Unit>

}