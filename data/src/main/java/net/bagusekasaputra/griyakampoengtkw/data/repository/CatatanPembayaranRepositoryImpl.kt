package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository

class CatatanPembayaranRepositoryImpl(
    private val localCatatanPembayaranDataSource: LocalCatatanPembayaranDataSource,
    private val remoteCatatanPembayaranDataSource: RemoteCatatanPembayaranDataSource,
    private val backupCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource,
): CatatanPembayaranRepository {

    override fun getCatatan(
        kavlingKode: String,
        dataMode: DataMode,
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
            val flowDataLama = flow<Result<CatatanPembayaran?>> {
                backupCatatanPembayaranDataSource.getCatatanPembayaran(kavlingKode)
                    .onSuccess {
                        emit(DataUtil.mapSingleResult(
                            originResult = Result.success(it),
                            targetMapper = ::mapCatatanPembayaran,
                        ))
                    }
            }

            when (dataMode) {
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.DATA_LAMA -> emitAll(flowDataLama)
            }
        }
    }

    override fun getBatch(listKavling: List<String>): Flow<Result<List<CatatanPembayaran>?>> {
        return callbackFlow {
            try {
                val listModels = mutableListOf<CatatanPembayaran>()

                listKavling.forEach { kavling ->
                    val model = getCatatan(kavling, DataMode.ONLINE)
                        .first().getOrThrow()

                    if (model != null) listModels.add(model)
                }

                trySendBlocking(Result.success(listModels.toList()))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
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

}