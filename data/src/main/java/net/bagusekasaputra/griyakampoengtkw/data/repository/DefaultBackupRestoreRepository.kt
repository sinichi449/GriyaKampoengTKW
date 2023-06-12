package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository

class DefaultBackupRestoreRepository(
    private val localDataSource: LocalBackupRestoreDataSource,
    private val remoteDataSource: RemoteBackupRestoreDataSource,
    private val cacheHelper: CacheHelper,
): BackupRestoreRepository {

    override suspend fun getListBackups(): Result<List<String>?> {
        return try {
            val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                cacheableLocal = localDataSource,
                cacheableRemote = remoteDataSource,
                onInvalid = {
                    localDataSource.invalidate().getOrThrow()
                }
            )
            val backups = if (isInvalidCache) {
                val remoteResult = remoteDataSource.getListBackup().getOrThrow()
                if (remoteResult.isNullOrEmpty()) {
                    null
                } else {
                    localDataSource.insertAll(remoteResult).getOrThrow()

                    localDataSource.getListBackups().getOrThrow()
                }
            } else {
                localDataSource.getListBackups().getOrThrow()
            }
            Result.success(backups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}