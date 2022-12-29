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

    override fun getBatch(listKalving: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>?>> {
        TODO("Not yet implemented")
    }

    override fun getAllByKavlingKode(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val flowOffline = flow<Result<List<BiayaMarketing>?>> {
                val localResult = localBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                localResult.onSuccess { biayaMarketingWithId ->
                    // Here we will parse the data from local data source from Map<Id, BiayaMarketing>
                    // to List<BiayaMarketing>. We need the id because it helps the write operation
                    // such as update and delete.

                    val mappedResult = mutableListOf<BiayaMarketing>()
                    biayaMarketingWithId?.keys?.forEach { id ->
                        val model = biayaMarketingWithId[id]

                        model?.let {
                            mappedResult.add(
                                BiayaMarketing(
                                    id = id,
                                    tanggal = it.tanggal,
                                    kavlingKode = it.kavlingKode,
                                    jenisBiaya = it.jenisBiaya,
                                    harga = it.harga.toString()
                                )
                            )
                        }
                    }

                    if (mappedResult.isEmpty())
                        emit(Result.success(null))
                    else
                        emit(Result.success(mappedResult))
                }

                localResult.onFailure {
                    emit(Result.failure(it))
                }
            }

            val flowOnline = flow<Result<List<BiayaMarketing>?>> {
                val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

                remoteResult.onSuccess {
                    // save to local data source
                    val models = remoteResult.getOrNull()
                    models?.forEach {
                        localBiayaMarketingDataSource.addBiayaMarketing(kavlingKode, it)
                    }

                    // emit from local to preserve the id
                    emitAll(flowOffline)
                }

                remoteResult.onFailure {
                    emit(Result.failure(it))

                    // on failure, emit from local
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val remoteResult = remoteBiayaMarketingDataSource.getAllBiayaMarketing(kavlingKode)

            emit(DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = ::mapBiayaMarketing,
            ))
        }
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBiayaMarketingDataSource.addBiayaMarketing(
                kavlingKode = biayaMarketing.kavlingKode,
                biayaMarketingModel = mapBiayaMarketing(biayaMarketing)
            )
            emit(remoteResult)
        }
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localBiayaMarketingDataSource.update(
                id = oldBiayaMarketing.id ?: -1L,
                newBiayaMarketingModel = mapBiayaMarketing(newBiayaMarketing),
            )
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.update(
                kavlingKode = oldBiayaMarketing.kavlingKode,
                oldBiayaMarketingModel = mapBiayaMarketing(oldBiayaMarketing),
                newBiayaMarketingModel = mapBiayaMarketing(newBiayaMarketing),
            )

            emit(remoteResult)
        }
    }

    override fun deleteSingle(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localBiayaMarketingDataSource.deleteSingle(biayaMarketing.id ?: -1L)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.deleteSingle(
                kavlingKode = kavlingKode,
                biayaMarketingModel = mapBiayaMarketing(biayaMarketing)
            )

            emit(remoteResult)
        }
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remoteBiayaMarketingDataSource.deleteAllBiayaMarketing(kavlingKode)

            emit(remoteResult)
        }
    }

    private fun mapBiayaMarketing(biayaMarketing: BiayaMarketing): BiayaMarketingModel {
        return biayaMarketing.let {
            BiayaMarketingModel(
                tanggal = it.tanggal,
                kavlingKode = it.kavlingKode,
                jenisBiaya = it.jenisBiaya,
                harga = NumberUtil.formatStringToLong(it.harga), // from UI layer, the harga is formatted into comma separated
            )
        }
    }

    private fun mapBiayaMarketing(biayaMarketingModel: BiayaMarketingModel): BiayaMarketing {
        return biayaMarketingModel.let {
            BiayaMarketing(
                kavlingKode = it.kavlingKode,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga.toString(),
            )
        }
    }


}