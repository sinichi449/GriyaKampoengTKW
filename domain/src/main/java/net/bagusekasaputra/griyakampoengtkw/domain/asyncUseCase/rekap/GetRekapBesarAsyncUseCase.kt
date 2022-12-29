package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapBesar
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.util.*

class GetRekapBesarAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<GetRekapBesarAsyncUseCase.Request, RekapBesar>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date?,
        val endDate: Date?,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<RekapBesar?>> {
        return flow {
            val mapPembayaran = pembayaranRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()
            val mapHargaKavling = hargaKavlingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()
            val mapFeeMarketing = feeMarketingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()
            val mapBiayaMarketing = biayaMarketingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()
            val listBiayaLain = biayaLainRepository.getAll(false)
                .first()
                .getOrThrow()


            /**
             * GET ALL REKAP BESAR
             */
            var totalUangMasuk = 0L
            var totalSisaBelumBayar = 0L
            var totalFeeMarketing = 0L
            var totalBiayaMarketing = 0L
            var totalBiayaLain = 0L

            request.kavlingList.forEach { kavling ->
                val listPembayaran = mapPembayaran?.get(kavling)
                val hargaKavling = mapHargaKavling?.get(kavling)
                val feeMarketing = mapFeeMarketing?.get(kavling)
                val listBiayaMarketing = mapBiayaMarketing?.get(kavling)

                val uangMasukKavling = if (listPembayaran != null) Pembayaran.hitungTotalUangMasuk(listPembayaran) else 0L
                val sisaBelumBayarKavling = if (hargaKavling != null) Pembayaran.hitungTotalSisaBelumBayar(hargaKavling, uangMasukKavling) else 0L
                val biayaMarketingKavling = if (listBiayaMarketing != null) BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing) else 0L

                totalUangMasuk += uangMasukKavling
                totalSisaBelumBayar += sisaBelumBayarKavling
                totalFeeMarketing += feeMarketing?.parsedBiayaMarketer ?: 0L
                totalBiayaMarketing += biayaMarketingKavling
            }

            listBiayaLain?.forEach {
                totalBiayaLain += it.harga
            }

            emit(Result.success(RekapBesar(
                totalUangMasuk,
                totalSisaBelumBayar,
                totalFeeMarketing,
                totalBiayaMarketing,
                totalBiayaLain
            )))
        }
    }
}