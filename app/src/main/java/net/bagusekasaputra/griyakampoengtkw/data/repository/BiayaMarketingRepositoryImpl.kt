package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.biayaMarketing.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiayaMarketingRepositoryImpl @Inject constructor(
    private val remoteBiayaMarketingDataSource: RemoteBiayaMarketingDataSource,
): BiayaMarketingRepository {

    override fun getAllByKavlingKode(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)
            val mapped = DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = ::mapBiayaMarketing
            )

            emit(mapped)
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

            val resultRemote = remoteBiayaMarketingDataSource.update(
                kavlingKode = oldBiayaMarketing.kavlingKode,
                oldBiayaMarketingModel = mapBiayaMarketing(oldBiayaMarketing),
                newBiayaMarketingModel = mapBiayaMarketing(newBiayaMarketing),
            )

            emit(resultRemote)
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
                val remoteResult = remoteBiayaMarketingDataSource.deleteSingle(
                    kavlingKode = kavlingKode,
                    timeMillis = biayaMarketing.timeMillis!!,
                )

                emit(remoteResult)
            }
        }
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)

            emit(remoteResult)
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