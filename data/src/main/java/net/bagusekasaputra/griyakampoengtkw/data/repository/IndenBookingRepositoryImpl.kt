package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
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

class IndenBookingRepositoryImpl(
    private val localDataSource: LocalIndenBookingDataSource,
    private val remoteDataSource: RemoteIndenBookingDataSource,
    localMetadata: LocalMetadataDataSource,
    remoteMetadata: RemoteMetadataDataSource,
): IndenBookingRepository {

    private val metadataHelper = MetadataHelper(localMetadata, remoteMetadata, "indenBooking")
    private var hasMetadataChecked = false

    override fun getAll(dataMode: DataMode): Flow<Result<List<IndenBooking>?>> {
        return flow {
            if (!hasMetadataChecked) {
                metadataHelper.checkCache {
                    localDataSource.deleteAll().onFailure {
                        it.printStackTrace()

                        Log.d("DEBUG_ME", "IndenBookingRepo::34 -> FAILED to clear all cache: ${it.message}")
                    }
                    metadataHelper.updateMetadata()
                }

                hasMetadataChecked = true
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
                val remoteResult = remoteDataSource.getAll()

                if (remoteResult.isSuccess) {
                    val listModel = remoteResult.getOrNull()

                    if (listModel != null) {
                        localDataSource.insertAll(listModel)
                    } else {
                        emit(Result.success(null))
                    }
                } else {
                    val errorCause = remoteResult.exceptionOrNull() ?: Throwable("IndenBookingRepo::getAll() -> Unknown ERROR trying to get to Remote Data Source!")
                    errorCause.printStackTrace()

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

    override fun insert(indenBooking: IndenBooking): Flow<Result<Nothing?>> {
        return flow {
            val model = MyObjectMapper.mapIndenBooking(indenBooking)
            val remoteResult = remoteDataSource.insert(model)

            if (remoteResult.isSuccess) {
                metadataHelper.updateMetadata()

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
}