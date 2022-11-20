package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository

class CatatanPembayaranRepositoryImpl(
    private val localCatatanPembayaranDataSource: LocalCatatanPembayaranDataSource,
    private val remoteCatatanPembayaranDataSource: RemoteCatatanPembayaranDataSource,
): CatatanPembayaranRepository {

    override fun getCatatan(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<CatatanPembayaran?>> {
        return flow {
            val flowOffline = flow<Result<CatatanPembayaran?>> {
                val localResult = localCatatanPembayaranDataSource.getCatatan(kavlingKode)

                emit(
                    DataUtil.mapSingleResult(localResult, ::mapCatatanPembayaran)
                )
            }

            val flowOnline = flow<Result<CatatanPembayaran?>> {
                // First, get from the remote
                val remoteResult = remoteCatatanPembayaranDataSource.getCatatan(kavlingKode)

                remoteResult.onSuccess {
                    // If success, the write to local data sources
                    val catatanModel = remoteResult.getOrNull()
                    if (catatanModel != null)
                        localCatatanPembayaranDataSource.addCatatan(kavlingKode, catatanModel)

                    // Then, emit the result
                    val mappedResult = DataUtil.mapSingleResult(
                        originResult = remoteResult,
                        targetMapper = ::mapCatatanPembayaran,
                    )
                    emit(mappedResult)
                }

                remoteResult.onFailure {
                    // If failed, first emit the failure message
                    emit(Result.failure(it))

                    // Then, get from local data source instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun addCatatan(
        kavlingKode: String,
        catatanPembayaran: CatatanPembayaran
    ): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteCatatanPembayaranDataSource.addCatatan(
                kavlingKode = kavlingKode,
                catatanPembayaranModel = mapCatatanPembayaran(catatanPembayaran),
            )

            emit(remoteResult)
        }
    }

    override fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // We should delete the local catatan too
            val localDelete = localCatatanPembayaranDataSource.deleteCatatan(kavlingKode)
            localDelete.onFailure {
                emit(Result.failure(it))
            }

            val remoteDelete = remoteCatatanPembayaranDataSource.deleteCatatan(kavlingKode)

            emit(remoteDelete)
        }
    }

    private fun mapCatatanPembayaran(catatanPembayaranModel: CatatanPembayaranModel): CatatanPembayaran {
        return catatanPembayaranModel.let {
            CatatanPembayaran(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }

    private fun mapCatatanPembayaran(catatanPembayaran: CatatanPembayaran): CatatanPembayaranModel {
        return catatanPembayaran.let {
            CatatanPembayaranModel(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }
}