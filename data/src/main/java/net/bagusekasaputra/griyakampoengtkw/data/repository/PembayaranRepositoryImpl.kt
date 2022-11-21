package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class PembayaranRepositoryImpl(
    private val localPembayaranDataSource: LocalPembayaranDataSource,
    private val remotePembayaranSource: RemotePembayaranSource,
): PembayaranRepository {

    override fun getAllPembayaran(
        kavlingKode: String,
        offline: Boolean
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            val flowOffline = flow<Result<List<Pembayaran>?>> {
                val localResult = localPembayaranDataSource.getAllPembayaran(kavlingKode)
                val mapResult = DataUtil.mapListResult(
                    originResult = localResult,
                    targetMapper = ::mapPembayaran,
                )

                emit(mapResult)
            }

            val flowOnline = flow<Result<List<Pembayaran>?>> {
                // First, we request to the remote
                val remoteResult = remotePembayaranSource.getAllPembayaran(kavlingKode)

                if (remoteResult.isSuccess) {
                    // If success, then we write to the local data
                    remoteResult.getOrNull()?.forEach {
                        localPembayaranDataSource.addPembayaranModel(kavlingKode, 0L, it)
                    }

                    // Then emit the result
                    val mapResult = DataUtil.mapListResult(
                        originResult = remoteResult,
                        targetMapper = ::mapPembayaran,
                    )
                    emit(mapResult)
                } else {
                    // If remote request is failed, we emit the error
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("Terjadi kesalahan tak diketahui pada server saat mendapatkan data Pembayaran!")
                    emit(Result.failure(errorCause))

                    // Then get from local instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return flow {
            val remoteResult = remotePembayaranSource.addPembayaranModel(
                kavlingKode,
                hargaKavling,
                pembayaranModel = mapPembayaran(pembayaran)
            )
            Log.d("DEBUG_ME", "Inserting pembayaran blblbl")

            if (remoteResult.isSuccess) {
                emit(Result.success(true))
            } else {
                val errorCause = remoteResult.exceptionOrNull()
                    ?: Throwable("Terjadi kesalahan tak diketahui saat menambah data Pembayaran!")
                emit(Result.failure(errorCause))
            }
        }
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return flow {
            // We need to update the local too!
            val localResult = localPembayaranDataSource.updatePembayaranModel(
                kavlingKode, mapPembayaran(oldPembayaran), mapPembayaran(newPembayaran)
            )
            localResult.onFailure {
                emit(Result.failure(it))
            }


            val remoteResult = remotePembayaranSource.updatePembayaranModel(
                kavlingKode,
                oldPembayaranModel = mapPembayaran(oldPembayaran),
                newPembayaranModel = mapPembayaran(newPembayaran)
            )
            remoteResult.onSuccess {
                emit(Result.success(true))
            }

            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
    ): Flow<Result<Boolean>> {
        return flow {
            // delete both from local and remote
            val localResult = localPembayaranDataSource.deletePembayaranModelByTermin(kavlingKode, termin)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranSource.deletePembayaranModelByTermin(kavlingKode, termin)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            // delete both from local and remote
            val localResult = localPembayaranDataSource.deleteAllPembayaranModel(kavlingKode)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranSource.deleteAllPembayaranModel(kavlingKode)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }


    private fun mapPembayaran(pembayaran: Pembayaran): PembayaranModel {
        return pembayaran.let {
            val pisah = pisahkanTerminDanUrutan(it.termin)

            return@let PembayaranModel(
                termin = pisah["jenis"]!!,
                urutan = pisah["urutan"]!!.toInt(),
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatStringToLong(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    private fun mapPembayaran(pembayaranModel: PembayaranModel): Pembayaran {
        return pembayaranModel.let {
            Pembayaran(
                termin = "${it.termin} ${it.urutan}",
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatLongToString(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    private fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
        val terminDanUrutan = termin.split(" ")
        return mapOf<String, String>(
            Pair("jenis", terminDanUrutan[0]),
            Pair("urutan", terminDanUrutan[1]),
        )
    }

}