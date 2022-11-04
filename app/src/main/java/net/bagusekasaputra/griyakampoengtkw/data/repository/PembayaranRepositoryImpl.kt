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

    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran
    ): Flow<Result<Boolean>> {
        return flow {
            emitAll(
                remotePembayaranSource.addPembayaranModel(kavlingKode, hargaKavling,
                    mapPembayaran(pembayaran))
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


    private fun mapPembayaran(pembayaran: Pembayaran): PembayaranModel {
        return pembayaran.let {
            PembayaranModel(
                termin = it.termin,
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatStringToLong(it.jumlahUangDibayar),
                keterangan = it.keterangan,
            )
        }
    }

}