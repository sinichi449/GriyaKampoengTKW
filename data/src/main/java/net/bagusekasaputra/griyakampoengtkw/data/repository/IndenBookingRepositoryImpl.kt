package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MetadataHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository
import kotlin.random.Random

class IndenBookingRepositoryImpl(
    private val localDataSource: LocalIndenBookingDataSource,
    private val remoteDataSource: RemoteIndenBookingDataSource,
    localMetadata: LocalMetadataDataSource,
    remoteMetadata: RemoteMetadataDataSource,
): IndenBookingRepository {

    private val metadataHelper = MetadataHelper(localMetadata, remoteMetadata, "indenBooking")
    private var hasMetadataChecked = false
    private var hasWipedLocalDataSource = false

    override fun getAll(dataMode: DataMode): Flow<Result<List<IndenBooking>?>> {
        return flow {
            if (!hasMetadataChecked) {
                hasMetadataChecked = true

                metadataHelper.checkCache {
                    metadataHelper.updateLocalMetadataOnInvalid()

                    localDataSource.deleteAll()
                        .onSuccess {
                            hasWipedLocalDataSource = true
                        }
                        .onFailure {
                        it.printStackTrace()

                        Log.d("DEBUG_ME", "IndenBookingRepo::34 -> FAILED to clear all cache: ${it.message}")
                    }
                }
            }

            val flowOffline = flow {
                val localResult = localDataSource.getAll()
                val mapResult = DataUtil.mapListResult(
                    originResult = localResult,
                    targetMapper = MyObjectMapper::mapIndenBooking,
                )

                emit(mapResult)
            }
            val flowOnline = flow {
                if (hasWipedLocalDataSource) {
                    hasWipedLocalDataSource = false

                    val remoteResult = remoteDataSource.getAll()
                    if (remoteResult.isSuccess) {
                        val listModel = remoteResult.getOrNull()
                        listModel?.forEach { model ->
                            val downloadImageResult = remoteDataSource.getFotoPembayaranPath(model)
                            if (downloadImageResult.isSuccess) {
                                downloadImageResult.getOrNull()?.also {
                                    model.fotoPembayaranPath = it
                                }
                            }
                        }

                        localDataSource.insertAll(listModel ?: emptyList())
                    } else {
                        val errorCause = remoteResult.exceptionOrNull() ?: Throwable("Unknown ERROR getAll() method IndenBookingRepository")
                        errorCause.printStackTrace()

                        emit(Result.failure(errorCause))
                    }
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

    override fun insert(indenBooking: IndenBooking): Flow<Result<Nothing?>> {
        return flow {
            val model = MyObjectMapper.mapIndenBooking(indenBooking)
            val remoteResult = remoteDataSource.insert(model)

            if (remoteResult.isSuccess) {
                metadataHelper.updateMetadataOnDataChange()

                val localResult = localDataSource.insert(model)

                if (localResult.isFailure) {
                    val errorLocal = localResult.exceptionOrNull()
                    errorLocal?.printStackTrace()

                    Log.d("DEBUG_ME", "IndenBookingRepositoryImpl::insert() -> Gagal menambahkan Inden Booking ke Local Data Source: ${errorLocal?.message}")
                }

                emit(Result.success(null))
            } else {
                val errorCause = remoteResult.exceptionOrNull() ?: Throwable("IndenBookingRepositoryImpl::insert() -> UNKNOWN ERROR: Gagal menambahkan Inden Booking ke Remote")
                emit(Result.failure(errorCause))
            }
        }
    }

    override fun delete(indenBooking: IndenBooking): Flow<Result<Nothing?>> {
        return callbackFlow {
            val model = MyObjectMapper.mapIndenBooking(indenBooking)
            remoteDataSource.delete(model)
                .onSuccess {
                    metadataHelper.updateMetadataOnDataChange()

                    val storageFotoResult = remoteDataSource.deleteFotoPembayaran(model)
                    if (storageFotoResult.isSuccess) {
                        val localResult = localDataSource.delete(model)
                        if (localResult.isFailure) {
                            val errorCause = localResult.exceptionOrNull() ?: Throwable("Unknwon Error delete() Inden Booking")
                            errorCause.printStackTrace()

                            Log.d("DEBUG_ME", "IndenBookingRepoImpl::delete() -> Error Local Data Source on delete: ${errorCause.message}")
                        }

                        trySendBlocking(Result.success(null))
                    } else {
                        val errorCause = storageFotoResult.exceptionOrNull() ?: Throwable("Unknown Error delete Foto Pembayaran Inden Booking")
                        errorCause.printStackTrace()
                        Log.d("DEBUG_ME", "IndenBookingRepoImpl::delete() -> Error deleting Inden Booking: ${errorCause.message}")

                        trySendBlocking(Result.failure(errorCause))
                    }
                }
                .onFailure {
                    it.printStackTrace()

                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun update(oldData: IndenBooking, newData: IndenBooking): Flow<Result<Nothing?>> {
        return flow {
            delay(3000L)

            val isSuccess = Random.nextBoolean()
            if (isSuccess) emit(Result.success(null))
            else emit(Result.failure(Throwable("RANDOM ERROR RepoImpl!")))
        }
    }
}