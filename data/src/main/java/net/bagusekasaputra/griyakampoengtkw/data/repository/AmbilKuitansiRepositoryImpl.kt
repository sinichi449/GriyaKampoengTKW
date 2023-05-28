package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository

class AmbilKuitansiRepositoryImpl(
    private val localDataSource: LocalAmbilKuitansiDataSource,
    private val remoteDataSource: RemoteAmbilKuitansiDataSource,
    private val cacheHelper: CacheHelper,
): AmbilKuitansiRepository {

    private val cacheTable = "ambilKuitansi"

    override suspend fun get(kavling: String, termin: String): Result<AmbilKuitansi?> {
        val isInvalidCache = cacheHelper.checkAndInvalidateCache(cacheTable, cacheTable,
            onInvalid = {
                localDataSource.deleteAll()
            }
        )
        val localModel = localDataSource.get(kavling, termin).getOrThrow()

        if (localModel == null || isInvalidCache) {
            val remoteModel = remoteDataSource.get(kavling, termin).getOrThrow()
            remoteModel?.also {
                localDataSource.insert(it)
            }
        }

        return DataUtil.mapSingleResult(
            originResult = localDataSource.get(kavling, termin),
            targetMapper = MyObjectMapper::mapAmbilKuitansi,
        )
    }

    override suspend fun insert(ambilKuitansi: AmbilKuitansi): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val model = MyObjectMapper.mapAmbilKuitansi(ambilKuitansi)
            val remoteResult = remoteDataSource.update(model)

            remoteResult
                .onSuccess {
                    localDataSource.update(model)

                    cacheHelper.updateMetadata(cacheTable, cacheTable)

                    trySendBlocking(Result.success(null))
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

}