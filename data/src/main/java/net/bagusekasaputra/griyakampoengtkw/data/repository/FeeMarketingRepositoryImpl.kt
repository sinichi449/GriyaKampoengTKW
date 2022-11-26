package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class FeeMarketingRepositoryImpl(
    private val localFeeMarketingDataSource: LocalFeeMarketingDataSource,
    private val remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource,
): FeeMarketingRepository {


    override fun getByKavlingKode(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<FeeMarketing?>> {
        return flow {
            val flowOffline = flow {
                val localResult = localFeeMarketingDataSource.getByKavlingKode(kavlingKode)

                emit(
                    DataUtil.mapSingleResult(localResult, ::mapFeeMarketing)
                )
            }

            val flowOnline = flow<Result<FeeMarketing?>> {
                // First, get from remote server
                val remoteResult = remoteFeeMarketingDataSource.getByKavlingKode(kavlingKode)

                remoteResult.onSuccess {
                    // If success, firstly write the data into local data source
                    val feeMarketingModel = remoteResult.getOrNull()
                    if (feeMarketingModel != null) {
                        val localWrite = localFeeMarketingDataSource.addFeeMarketing(kavlingKode, feeMarketingModel)
                        localWrite.onFailure {
                            emit(Result.failure(it))
                        }
                    }

                    // Then, emit the result
                    val mappedResult = DataUtil.mapSingleResult(
                        originResult = remoteResult,
                        targetMapper = ::mapFeeMarketing,
                    )
                    emit(mappedResult)
                }

                remoteResult.onFailure {
                    // If fails, then emit the error
                    emit(Result.failure(it))

                    // Then, emit from local data source instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<FeeMarketing?>> {
        return flow {
            // First, get from remote server
            val remoteResult = remoteFeeMarketingDataSource.getByKavlingKode(kavlingKode)

            val mappedResult = DataUtil.mapSingleResult(
                originResult = remoteResult,
                targetMapper = ::mapFeeMarketing,
            )
            emit(mappedResult)
        }
    }

    override fun addFeeMarketing(feeMarketing: FeeMarketing): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteFeeMarketingDataSource.addFeeMarketing(
                kavlingKode = feeMarketing.kavlingKode,
                feeMarketingModel = mapFeeMarketing(feeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun updateFeeMarketing(
        oldFeeMarketing: FeeMarketing,
        newFeeMarketing: FeeMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            // We need to update the local data source too
            val localUpdate = localFeeMarketingDataSource.updateFeeMarketing(
                kavlingKode = oldFeeMarketing.kavlingKode,
                oldFeeMarketingModel = mapFeeMarketing(oldFeeMarketing),
                newFeeMarketingModel = mapFeeMarketing(newFeeMarketing),
            )
            localUpdate.onFailure {
                emit(Result.failure(it))
            }


            val remoteResult = remoteFeeMarketingDataSource.updateFeeMarketing(
                kavlingKode = oldFeeMarketing.kavlingKode,
                oldFeeMarketingModel = mapFeeMarketing(oldFeeMarketing),
                newFeeMarketingModel = mapFeeMarketing(newFeeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            val localDelete = localFeeMarketingDataSource.deleteFeeMarketing(kavlingKode)
            localDelete.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteFeeMarketingDataSource.deleteFeeMarketing(kavlingKode)

            emit(remoteResult)
        }
    }

    private fun mapFeeMarketing(feeMarketingModel: FeeMarketingModel): FeeMarketing =
        feeMarketingModel.let {
            FeeMarketing(
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toString(),
                tanggalPenerimaan = it.getTanggalStr(),
            )
        }

    private fun mapFeeMarketing(feeMarketing: FeeMarketing) =
        feeMarketing.let {
            FeeMarketingModel(
                timeMillis = it.getTimemillisTanggalPenerimaan(),
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toLong(),
            )
        }

}