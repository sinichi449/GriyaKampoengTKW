package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
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
                    val listPembayaran = listPembayaranModel?.map { pembayaranModel -> mapPembayaran(pembayaranModel) }

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
        TODO("Not yet implemented")
    }


    private fun mapPembayaran(pembayaran: Pembayaran): PembayaranModel {
        return pembayaran.let {
            PembayaranModel(
                termin = it.termin,
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
                termin = it.termin,
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatLongToString(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

}