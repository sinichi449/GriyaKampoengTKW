package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapKavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class KavlingCatatanPembayaranRepositoryImpl(
    private val localKavlingCatatanPembayaranDataSource: LocalKavlingCatatanPembayaranDataSource,
    private val remoteKavlingCatatanPembayaranDataSource: RemoteKavlingCatatanPembayaranDataSource,
): KavlingCatatanPembayaranRepository {

    override fun getCatatan(
        kavlingKode: String,
        dataMode: DataMode,
    ): Flow<Result<KavlingCatatanPembayaran?>> {
        return flow {
            val flowOffline = flow<Result<KavlingCatatanPembayaran?>> {
                val localResult = localKavlingCatatanPembayaranDataSource.getCatatan(kavlingKode)

                emit(
                    DataUtil.mapSingleResult(localResult, ::mapKavlingCatatanPembayaran)
                )
            }
            val flowOnline = flow<Result<KavlingCatatanPembayaran?>> {
                // First, get from the remote
                val remoteResult = remoteKavlingCatatanPembayaranDataSource.getCatatan(kavlingKode)

                remoteResult.onSuccess {
                    // If success, the write to local data sources
                    val catatanModel = remoteResult.getOrNull()
                    if (catatanModel != null)
                        localKavlingCatatanPembayaranDataSource.addCatatan(kavlingKode, catatanModel)

                    // Then, emit the result
                    val mappedResult = DataUtil.mapSingleResult(
                        originResult = remoteResult,
                        targetMapper = ::mapKavlingCatatanPembayaran,
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

            when (dataMode) {
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.DATA_LAMA -> TODO("Not yet implemented")
            }
        }
    }

    override fun getBatch(listKavling: List<String>): Flow<Result<List<KavlingCatatanPembayaran>?>> {
        return callbackFlow {
            try {
                val listModels = mutableListOf<KavlingCatatanPembayaran>()

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
        kavlingCatatanPembayaran: KavlingCatatanPembayaran
    ): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteKavlingCatatanPembayaranDataSource.addCatatan(
                kavlingKode = kavlingKode,
                kavlingCatatanPembayaranModel = mapKavlingCatatanPembayaran(kavlingCatatanPembayaran),
            )

            emit(remoteResult)
        }
    }

    override fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // We should delete the local catatan too
            val localDelete = localKavlingCatatanPembayaranDataSource.deleteCatatan(kavlingKode)
            localDelete.onFailure {
                emit(Result.failure(it))
            }

            val remoteDelete = remoteKavlingCatatanPembayaranDataSource.deleteCatatan(kavlingKode)

            emit(remoteDelete)
        }
    }

}