package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.feeMarketing.RemoteFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeeMarketingRepositoryImpl @Inject constructor(
    private val remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource,
): FeeMarketingRepository {


    override fun getByKavlingKode(kavlingKode: String): Flow<Result<FeeMarketing?>> {
        return flow {
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
            val timeMillis = System.currentTimeMillis()
            val remoteResult = remoteFeeMarketingDataSource.addFeeMarketing(
                kavlingKode = feeMarketing.kavlingKode,
                feeMarketingModel = mapFeeMarketing(timeMillis, feeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun updateFeeMarketing(
        oldFeeMarketing: FeeMarketing,
        newFeeMarketing: FeeMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            val timeMillis = System.currentTimeMillis()
            val remoteResult = remoteFeeMarketingDataSource.updateFeeMarketing(
                kavlingKode = oldFeeMarketing.kavlingKode,
                oldFeeMarketingModel = mapFeeMarketing(timeMillis, oldFeeMarketing),
                newFeeMarketingModel = mapFeeMarketing(timeMillis, newFeeMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteFeeMarketingDataSource.deleteFeeMarketing(kavlingKode)

            emit(remoteResult)
        }
    }

    private fun mapFeeMarketing(feeMarketingModel: FeeMarketingModel) =
        feeMarketingModel.let {
            FeeMarketing(
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toString(),
            )
        }

    private fun mapFeeMarketing(timeMillis: Long, feeMarketing: FeeMarketing) =
        feeMarketing.let {
            FeeMarketingModel(
                timeMillis = timeMillis,
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toLong(),
            )
        }

}