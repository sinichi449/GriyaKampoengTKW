package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class BiayaLainRepositoryImpl(
    private val remoteBiayaLainDataSource: RemoteBiayaLainDataSource,
): BiayaLainRepository {

    override fun getAll(offline: Boolean): Flow<Result<List<BiayaLain>?>> {
        return flow {
            val remoteResult = remoteBiayaLainDataSource.getAll().map { result ->
                DataUtil.mapListResult(
                    originResult = result,
                    targetMapper = ::mapBiayaLain,
                )
            }

            emitAll(remoteResult)

            // TODO: If offline get from local data source
        }
    }

    override fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>> {
        return flow {
            val remoteResult = remoteBiayaLainDataSource.getSingle(jenisBiaya)

            emit(
                DataUtil.mapSingleResult(
                    originResult = remoteResult,
                    targetMapper = ::mapBiayaLain,
                )
            )
            // TODO: If offline get from local data source
        }
    }

    override fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBiayaLainDataSource.addBiaya(mapBiayaLain(biayaLain))

            emit(remoteResult)
        }
    }

    override fun updateBiayaLain(
        oldBiayaLain: BiayaLain,
        newBiayaLain: BiayaLain,
    ): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBiayaLainDataSource.update(
                oldModel = mapBiayaLain(oldBiayaLain),
                newModel = mapBiayaLain(newBiayaLain),
            )

            emit(remoteResult)
        }
    }

    override fun deleteBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteBiayaLainDataSource.delete(mapBiayaLain(biayaLain))

            emit(remoteResult)
        }
    }

    private fun mapBiayaLain(model: BiayaLainModel): BiayaLain {
        return model.let {
            BiayaLain(
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
                tanggal = it.tanggal,
            )
        }
    }

    private fun mapBiayaLain(biayaLain: BiayaLain): BiayaLainModel {
        return biayaLain.let {
            BiayaLainModel(
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
                tanggal = it.tanggal,
            )
        }
    }
}