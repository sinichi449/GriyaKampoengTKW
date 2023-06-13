package net.bagusekasaputra.griyakampoengtkw.data.repository

import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil.networkBoundResources
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository
import java.io.File

class PengembalianRepositoryImpl(
    private val localDataSource: LocalPengembalianDataSource,
    private val remoteDataSource: RemotePengembalianDataSource,
    private val cacheHelper: CacheHelper,
    externalFilesDir: File?,
): PengembalianRepository {

    private val dstDir = File(externalFilesDir, PengembalianModel.DST_DIR)
    private var shouldCheckCache = true

    init {
        // Create base directory if not exist
        if (!dstDir.exists()) dstDir.mkdirs()
    }

    override fun getAsFlow(keyId: String, dataMode: DataMode): Flow<Result<Pengembalian?>> {
        return networkBoundResources(
            shouldFetch = {
                if (shouldCheckCache) {
                    shouldCheckCache = false

                    cacheHelper.checkAndInvalidateCache(
                        cacheableLocal = localDataSource,
                        cacheableRemote = remoteDataSource,
                        onInvalid = {
                            localDataSource.invalidate()
                        }
                    )
                } else {
                    false
                }
            },
            query = { localDataSource.get(keyId) },
            fetch = { remoteDataSource.get(keyId) },
            saveFetchResult = { remoteModel ->
                if (remoteModel != null) {
                    // Download bukti foto
                    val downloadDestination = remoteModel.downloadDstUri()
                    val downloadSucceed = remoteDataSource.downloadImage(keyId, downloadDestination)

                    val localModel = if (downloadSucceed)
                        remoteModel.copy(uri = downloadDestination) else remoteModel

                    localDataSource.insert(localModel)
                } else {
                    Result.success(Unit)
                }
            }
        ).map {
            DataUtil.mapSingleResult(
                originResult = it,
                targetMapper = MyObjectMapper::mapPengembalian
            )
        }
    }

    override suspend fun getKeyIds(dataMode: DataMode): Result<List<String>?> {
        return runCatching {
            val localResult = localDataSource.getKeyIds().getOrThrow()

            if (localResult.isNullOrEmpty()) {
                remoteDataSource.getKeyIds().getOrThrow()
            } else {
                localResult
            }
        }
    }

    override suspend fun getAll(dataMode: DataMode): Result<List<Pengembalian?>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(pengembalian: Pengembalian): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(keyId: String, new: Pengembalian): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(kavling: String) {
        TODO("Not yet implemented")
    }

    private fun PengembalianModel.downloadDstUri(): String {
        return File(dstDir, this.fileName).toUri().toString()
    }
}