package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PembayaranRepositoryImpl @Inject constructor(
    private val remotePembayaranSource: RemotePembayaranSource
): PembayaranRepository {

    override fun getAllPembayaran(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        return callbackFlow {
            remotePembayaranSource.getAllPembayaran(
                kavlingKode = kavlingKode,
                onSuccess = { listPembayaranModel ->
                    val listPembayaran = listPembayaranModel?.map { mapPembayaran(it) }

                    if (listPembayaran != null) {
                        trySendBlocking(Result.success(listPembayaran))
                    } else {
                        trySendBlocking(Result.success(null))
                    }
                },
                onFailure = {
                    trySendBlocking(Result.failure(it))
                }
            )

            awaitClose {  }
        }
    }

    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            val pembayaranModel = mapPembayaran(pembayaran)

            remotePembayaranSource.addPembayaranModel(
                kavlingKode = kavlingKode,
                hargaKavling = hargaKavling,
                pembayaranModel = pembayaranModel,
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = {  trySendBlocking(Result.failure(it)) },
            )

            awaitClose {  }
        }
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            remotePembayaranSource.updatePembayaranModel(
                kavlingKode = kavlingKode,
                oldPembayaranModel = mapPembayaran(oldPembayaran),
                newPembayaranModel = mapPembayaran(newPembayaran),
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = { trySendBlocking(Result.failure(it)) },
            )

            awaitClose {  }
        }
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            remotePembayaranSource.deletePembayaranModelByTermin(
                kavlingKode = kavlingKode,
                termin = termin,
                onSuccess = {
                    trySendBlocking(Result.success(true))
                },
                onFailure = {
                    trySendBlocking(Result.failure(it))
                }
            )

            awaitClose {  }
        }
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        return callbackFlow {
            remotePembayaranSource.deleteAllPembayaranModel(
                kavlingKode = kavlingKode,
                onSuccess = {
                    trySendBlocking(Result.success(true))
                },
                onFailure = {
                    trySendBlocking(Result.failure(it))
                }
            )

            awaitClose {  }
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