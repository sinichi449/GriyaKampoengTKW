package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MetadataHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository

class BaselinePembayaranRepositoryImpl(
    private val localDataSource: LocalBaselinePembayaranDataSource,
    private val remoteDataSource: RemoteBaselinePembayaranDataSource,
    localMetadata: LocalMetadataDataSource,
    remoteMetadata: RemoteMetadataDataSource,
): BaselinePembayaranRepository {

    private val metadataHelper = MetadataHelper(localMetadata, remoteMetadata, "baselinePembayaran")
    private var hasMetadataChecked = false

    override fun get(kavling: String, dataMode: DataMode): Flow<Result<BaselinePembayaran?>> {
        return flow {
            if (!hasMetadataChecked) {
                hasMetadataChecked = true

                metadataHelper.checkCache {
                    metadataHelper.updateLocalMetadataOnInvalid()

                    localDataSource.deleteAll().onFailure {
                        it.printStackTrace()

                        Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge \"Baseline Pembayaran\" cache: ${it.message}")
                    }
                }
            }

            val flowOffline = flow {
                val localResult = localDataSource.get(kavling)
                val mapResult = DataUtil.mapSingleResult(
                    originResult = localResult,
                    targetMapper = MyObjectMapper::mapBaselinePembayaran
                )

                emit(mapResult)
            }
            val flowOnline = flow {
                val remoteResult = remoteDataSource.get(kavling)

                if (remoteResult.isSuccess) {
                    val model = remoteResult.getOrThrow()

                    if (model != null) {
                        localDataSource.insert(model)
                    } else {
                        emit(Result.success(null))
                    }
                } else {
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("BaselinePembayaranRepo::65 -> Unknown error Baseline Pembayaran")
                    emit(Result.failure(errorCause))
                }

                emitAll(flowOffline)
            }
            val flowDataLama = flow {
                // TODO
                emit(Result.success(null))
            }

            when (dataMode) {
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.DATA_LAMA -> emitAll(flowDataLama)
            }
        }
    }

    override fun insert(baselinePembayaran: BaselinePembayaran): Flow<Result<Nothing?>> {
        return flow {
            val model = MyObjectMapper.mapBaselinePembayaran(baselinePembayaran)
            val remoteResult = remoteDataSource.insert(model)

            if (remoteResult.isSuccess) {
                metadataHelper.updateLocalMetadataOnInvalid()

                val localResult = localDataSource.insert(model)
                if (localResult.isFailure) {
                    val errorLocal = localResult.exceptionOrNull()
                    errorLocal?.printStackTrace()

                    Log.d("DEBUG_ME", "BaselinePembayaranRepo::98 -> Gagal menulis cache ${errorLocal?.message}")
                }
                emit(Result.success(null))
            } else {
                val errorCause = remoteResult.exceptionOrNull()
                    ?: Throwable("BaselinePembayaran::97 -> Unknown ERROR WRITE Baseline Pembayaran to Remote")
                emit(Result.failure(errorCause))
            }
        }
    }

    override suspend fun getAngsuran(kavling: String, dataMode: DataMode): Long? {
        return flow<Long?> {
            val baselinePembayaran = localDataSource.get(kavling)
                .getOrNull()

            if (baselinePembayaran != null) {
                emit(baselinePembayaran.jumlahUang)
            } else {
                emit(0L)
            }
        }.first()
    }

    override suspend fun refreshCache(kavlings: List<Kavling>): Result<Nothing?> {
        return try {
            localDataSource.deleteAll().getOrThrow()

            val baselinePembayaranModels = mutableListOf<BaselinePembayaranModel>()
            kavlings.forEach {  kavling ->
                val kavlingKode = kavling.kode
                val baselinePembayaranModel = remoteDataSource.get(kavlingKode).getOrThrow()

                baselinePembayaranModel?.also { baselinePembayaranModels.add(it) }
            }

            localDataSource.addAll(baselinePembayaranModels).getOrThrow()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}