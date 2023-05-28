package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository
import kotlin.random.Random

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
        delay(5000L)

        val randomSuccess = Random.nextBoolean()
        return if (randomSuccess) {
            Result.success(null)
        } else {
            Result.failure(Throwable("Random failure!!"))
        }
    }

}