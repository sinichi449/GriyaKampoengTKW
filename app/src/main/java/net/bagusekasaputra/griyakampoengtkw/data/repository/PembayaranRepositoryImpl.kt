package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.PembayaranModel
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

    override fun getPembayaran(kavlingKode: String): Flow<Result<Pembayaran>> {
        TODO("Not yet implemented")
    }

    override fun addPembayaran(kavlingKode: String, pembayaran: Pembayaran): Flow<Result<Boolean>> {
        return flow {
            emitAll(
                remotePembayaranSource.addPembayaranModel(kavlingKode, mapPembayaran(pembayaran))
            )
        }
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getLatestTotalUangMasuk(kavlingKode: String): Flow<Result<Long>> {
        return flow {
            emitAll(remotePembayaranSource.getLatestTotalUangMasuk(kavlingKode))
        }
    }

    private fun mapPembayaran(pembayaran: Pembayaran): PembayaranModel {
        return pembayaran.let {
            PembayaranModel(
                termin = it.termin,
                tanggal = it.tanggal,
                jumlahUang = NumberUtil.formatStringToLong(it.jumlahUang),
                totalUangMasuk = NumberUtil.formatStringToLong(it.totalUangMasuk),
                presentase = it.presentase,
                keterangan = it.keterangan,
            )
        }
    }

}