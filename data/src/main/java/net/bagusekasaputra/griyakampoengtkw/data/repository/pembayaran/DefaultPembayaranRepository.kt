package net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class DefaultPembayaranRepository(
    private val localDataSource: LocalPembayaranDataSource,
    private val remoteDataSource: RemotePembayaranSource,
    private val cacheHelper: CacheHelper,
): PembayaranRepository {

    private val cacheTable = "formPembayaran"

    override suspend fun getByKavlingAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<Pembayaran?> {
        TODO("Not yet implemented")
    }

    override fun getAllPembayaran(
        kavlingKode: String,
        dataMode: DataMode
    ): Flow<Result<List<Pembayaran>?>> {
        return callbackFlow {
            try {
                trySendBlocking(when (dataMode) {
                    DataMode.OFFLINE -> {
                        val localModels = localDataSource.getAllPembayaran(kavlingKode)
                            .getOrThrow()
                        val pembayaranModels = localModels?.map { model ->
                            MyObjectMapper.mapPembayaran(model)
                        }

                        Result.success(pembayaranModels)
                    }
                    DataMode.ONLINE -> {
                        val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                            cacheTable, cacheTable,
                            onInvalid =  {
                                localDataSource.deleteAll()
                            }
                        )
                        val localModels = localDataSource.getAllPembayaran(kavlingKode)
                            .getOrThrow()
                        val pembayaranList: List<Pembayaran>?

                        if (isInvalidCache || localModels.isNullOrEmpty()) {
                            val remoteModels = remoteDataSource.getAllPembayaran(kavlingKode)
                                .getOrThrow()
                            pembayaranList = if (!remoteModels.isNullOrEmpty()) {
                                localDataSource.addAllPembayaranModel(kavlingKode, remoteModels)
                                    .getOrThrow()

                                val refreshedLocalModel = localDataSource.getAllPembayaran(kavlingKode)
                                    .getOrThrow()
                                refreshedLocalModel?.map {
                                    MyObjectMapper.mapPembayaran(it)
                                }
                            } else {
                                null
                            }
                        } else {
                            pembayaranList = localModels.map {
                                MyObjectMapper.mapPembayaran(it)
                            }
                        }

                        Result.success(pembayaranList)
                    }
                    DataMode.DATA_LAMA -> Result.failure(UnsupportedOperationException("Fitur Data Lama pembayaran belum diaktifkan!"))
                })
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        TODO("Not yet implemented")
    }

    override fun addPembayaran(kavlingKode: String, pembayaran: Pembayaran): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePembayaran(
        kavlingKode: String,
        newPembayaran: Pembayaran
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshCache(kavlings: List<String>): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override fun fromBackupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<Pembayaran>?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        pembayaran: Pembayaran
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}