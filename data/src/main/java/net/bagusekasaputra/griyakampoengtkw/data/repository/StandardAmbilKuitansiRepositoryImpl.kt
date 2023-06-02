package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StandardAmbilKuitansiRepository

class StandardAmbilKuitansiRepositoryImpl(
    private val localDataSource: LocalStandardAmbilKuitansiDataSource,
    private val remoteDataSource: RemoteStandardAmbilKuitansiDataSource,
    private val cacheHelper: CacheHelper,
): StandardAmbilKuitansiRepository {

    private val cacheTable = "ambilKuitansi"

    override suspend fun get(kavling: String, termin: String): Result<StandardAmbilKuitansi?> {
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
            targetMapper = MyObjectMapper::mapStandardAmbilKuitansi,
        )
    }

    override suspend fun insert(standardAmbilKuitansi: StandardAmbilKuitansi): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val model = MyObjectMapper.mapStandardAmbilKuitansi(standardAmbilKuitansi)
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

    override suspend fun delete(kavling: String, termin: String): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // Delete on remote
            remoteDataSource.delete(kavling, termin)
                .onSuccess {
                    // Delete on Local
                    localDataSource.delete(kavling, termin)
                        .onSuccess {
                            // Update Cache
                            cacheHelper.updateMetadata(cacheTable, cacheTable)
                                .onSuccess {
                                    trySendBlocking(Result.success(it))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

}