package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStatusPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository

class StatusPembayaranRepositoryImpl(
    private val remoteDataSource: RemoteStatusPembayaranDataSource,
): StatusPembayaranRepository {

    override fun get(kavling: String, dataMode: DataMode): Flow<Result<StatusPembayaran?>> {
        return flow {
            val flowOffline = flow<Result<StatusPembayaran?>> {
                // TODO
            }
            val flowOnline = flow<Result<StatusPembayaran?>> {
                val remoteResult = remoteDataSource.get(kavling)

                remoteResult.onSuccess { statusPembayaranModel ->
                    val statusPembayaran = statusPembayaranModel?.let {
                        MyObjectMapper.mapStatusPembayaran(it)
                    }

                    this.emit(Result.success(statusPembayaran))
                }

                remoteResult.onFailure {
                    this.emit(Result.failure(it))
                }
            }
            val flowDataLama = flow<Result<StatusPembayaran?>> {
                // TODO
            }

            when (dataMode) {
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.DATA_LAMA -> emitAll(flowDataLama)
            }
        }
    }
}