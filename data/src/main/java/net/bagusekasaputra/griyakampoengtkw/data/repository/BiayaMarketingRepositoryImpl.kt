package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository

class BiayaMarketingRepositoryImpl(
    private val localBiayaMarketingDataSource: LocalBiayaMarketingDataSource,
    private val remoteBiayaMarketingDataSource: RemoteBiayaMarketingDataSource,
): BiayaMarketingRepository {

    override fun getAllByKavlingKode(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val flowOffline = flow<Result<List<BiayaMarketing>?>> {
                val localResult = localBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                emit(
                    DataUtil.mapListResult(localResult, ::mapBiayaMarketing)
                )
            }

            val flowOnline = flow<Result<List<BiayaMarketing>?>> {
                // First, fetch from the remote
                val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                remoteResult.onSuccess {
                    // if successfully fetching the data from the remote repository,
                    // first, save to local data source
                    val biayaMarketingModel = remoteResult.getOrNull()
                    biayaMarketingModel?.forEach {
                        localBiayaMarketingDataSource.addBiayaMarketing(kavlingKode, it)
                    }

                    // Then emit the result
                    val mapped = DataUtil.mapListResult(
                        originResult = remoteResult,
                        targetMapper = ::mapBiayaMarketing
                    )
                    emit(mapped)
                }

                remoteResult.onFailure {
                    // if fetching from remote fails, emit the error message
                    emit(Result.failure(it))

                    // Then, fetch from local data source instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        return flow {
            val resultRemote = remoteBiayaMarketingDataSource.addBiayaMarketing(
                kavlingKode = biayaMarketing.kavlingKode,
                biayaMarketingModel = mapBiayaMarketing(biayaMarketing),
            )

            emit(resultRemote)
        }
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            // We need to set the timeMillis of newBiayaMarketing to prevent
            // a difference of timeMillis with the oldBiayaMarketing
            newBiayaMarketing.timeMillis = oldBiayaMarketing.timeMillis

            val mappedOld = mapBiayaMarketing(oldBiayaMarketing)
            val mappedNew = mapBiayaMarketing(newBiayaMarketing)

            // We need to update the data on the local data source too
            val updateLocal = localBiayaMarketingDataSource.update(
                kavlingKode = oldBiayaMarketing.kavlingKode,
                oldBiayaMarketingModel = mappedOld,
                newBiayaMarketingModel = mappedNew,
            )
            updateLocal.onFailure {
                emit(Result.failure(it))
            }


            val remoteUpdate = remoteBiayaMarketingDataSource.update(
                kavlingKode = oldBiayaMarketing.kavlingKode,
                oldBiayaMarketingModel = mappedOld,
                newBiayaMarketingModel = mappedNew,
            )

            emit(remoteUpdate)
        }
    }

    override fun deleteSingle(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            // If time millis is null from BiayaMarketing entity, I will send the error instead.
            if (biayaMarketing.timeMillis == null) {
                emit(Result.failure(Exception("ERROR: Time millis tidak ditemukan")))
            } else {
                // We need to delete from local data source too
                val deleteLocal = localBiayaMarketingDataSource.deleteSingle(
                    kavlingKode = kavlingKode,
                    timeMillis = biayaMarketing.timeMillis!!,
                )
                deleteLocal.onFailure {
                    emit(Result.failure(it))
                }


                val deleteRemote = remoteBiayaMarketingDataSource.deleteSingle(
                    kavlingKode = kavlingKode,
                    timeMillis = biayaMarketing.timeMillis!!,
                )

                emit(deleteRemote)
            }
        }
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // We need to delete from the local data source too
            val deleteAllLocal = localBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)
            deleteAllLocal.onFailure {
                emit(Result.failure(it))
            }

            val deleteAllRemote = remoteBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)

            emit(deleteAllRemote)
        }
    }

    private fun mapBiayaMarketing(biayaMarketingModel: BiayaMarketingModel): BiayaMarketing {
        return biayaMarketingModel.let {
            BiayaMarketing(
                timeMillis = it.timeMillis,
                kavlingKode = it.kavlingKode,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga.toString(),
            )
        }
    }

    private fun mapBiayaMarketing(biayaMarketing: BiayaMarketing): BiayaMarketingModel {
        return biayaMarketing.let {
            BiayaMarketingModel(
                timeMillis = it.timeMillis ?: System.currentTimeMillis(),
                kavlingKode = it.kavlingKode,
                jenisBiaya = it.jenisBiaya,
                harga = NumberUtil.formatStringToLong(it.harga),
            )
        }
    }
}