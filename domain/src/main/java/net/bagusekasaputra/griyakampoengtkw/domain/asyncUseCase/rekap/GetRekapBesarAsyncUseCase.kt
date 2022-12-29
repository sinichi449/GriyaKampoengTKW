package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.util.*
import kotlin.Result

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

            val rekapBesar = when (request.periode) {
                PeriodeRekap.SEMUA -> getAllRekapBesar(request.kavlingList, mapPembayaran, mapHargaKavling, mapFeeMarketing, mapBiayaMarketing, listBiayaLain)
                PeriodeRekap.TAHUN_INI -> getTahunIni(request.kavlingList, mapPembayaran, mapHargaKavling, mapFeeMarketing, mapBiayaMarketing, listBiayaLain)
                PeriodeRekap.MINGGU_INI -> getMingguIni(request.kavlingList, mapPembayaran, mapHargaKavling, mapFeeMarketing, mapBiayaMarketing, listBiayaLain)
                PeriodeRekap.BULAN_INI -> getBulanIni(request.kavlingList, mapPembayaran, mapHargaKavling, mapFeeMarketing, mapBiayaMarketing, listBiayaLain)
                PeriodeRekap.CUSTOM -> getCustomRekap(request.kavlingList, request.startDate, request.endDate, mapPembayaran, mapHargaKavling, mapFeeMarketing, mapBiayaMarketing, listBiayaLain)
            }
            emit(Result.success(rekapBesar))
        }
    }

    private fun getAllRekapBesar(
        kavlingList: List<String>,
        mapPembayaran: Map<String, List<Pembayaran>?>?,
        mapHargaKavling: Map<String, HargaKavling?>?,
        mapFeeMarketing: Map<String, FeeMarketing?>?,
        mapBiayaMarketing: Map<String, List<BiayaMarketing>?>?,
        listBiayaLain: List<BiayaLain>?,
    ): RekapBesar {
        var totalUangMasuk = 0L
        var totalSisaBelumBayar = 0L
        var totalFeeMarketing = 0L
        var totalBiayaMarketing = 0L
        var totalBiayaLain = 0L

        kavlingList.forEach { kavling ->
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

        return RekapBesar(
            totalUangMasuk,
            totalSisaBelumBayar,
            totalFeeMarketing,
            totalBiayaMarketing,
            totalBiayaLain
        )
    }

    private fun getTahunIni(
        kavlingList: List<String>,
        mapPembayaran: Map<String, List<Pembayaran>?>?,
        mapHargaKavling: Map<String, HargaKavling?>?,
        mapFeeMarketing: Map<String, FeeMarketing?>?,
        mapBiayaMarketing: Map<String, List<BiayaMarketing>?>?,
        listBiayaLain: List<BiayaLain>?,
    ): RekapBesar {
        var totalUangMasuk = 0L
        var totalSisaBelumBayar = 0L
        var totalFeeMarketing = 0L
        var totalBiayaMarketing = 0L
        var totalBiayaLain = 0L

        val tahunSekarang = Calendar.getInstance().get(Calendar.YEAR)

        kavlingList.forEach { kavling ->
            val listPembayaran = mapPembayaran?.get(kavling)?.filter { pembayaran ->
                val tahunPembayaran = Calendar.getInstance().let {
                    it.time = pembayaran.tanggal.toDate()
                    it.get(Calendar.YEAR)
                }

                tahunPembayaran == tahunSekarang
            }
            val hargaKavling = mapHargaKavling?.get(kavling)
            val feeMarketing = mapFeeMarketing?.get(kavling)?.let {
                val tahunPenerimaan = Calendar.getInstance().run {
                    time = it.tanggalPenerimaan.toDate()
                    get(Calendar.YEAR)
                }

                if (tahunPenerimaan == tahunSekarang) {
                    it
                } else {
                    null
                }
            }
            val listBiayaMarketing = mapBiayaMarketing?.get(kavling)?.filter { biayaMarketing ->
                val tahunBiayaMarketing = Calendar.getInstance().run {
                    time = biayaMarketing.tanggal.toDate()
                    get(Calendar.YEAR)
                }

                tahunBiayaMarketing == tahunSekarang
            }

            val uangMasukKavling = if (listPembayaran != null) Pembayaran.hitungTotalUangMasuk(listPembayaran) else 0L
            val sisaBelumBayarKavling = if (hargaKavling != null) Pembayaran.hitungTotalSisaBelumBayar(hargaKavling, uangMasukKavling) else 0L
            val biayaMarketingKavling = if (listBiayaMarketing != null) BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing) else 0L

            totalUangMasuk += uangMasukKavling
            totalSisaBelumBayar += sisaBelumBayarKavling
            totalFeeMarketing += feeMarketing?.parsedBiayaMarketer ?: 0L
            totalBiayaMarketing += biayaMarketingKavling
        }

        listBiayaLain?.filter { biayaLain ->
            val tahunBiayaLain = Calendar.getInstance().run {
                time = biayaLain.tanggal.toDate()
                get(Calendar.YEAR)
            }

            tahunBiayaLain == tahunSekarang
        }
            ?.forEach {
                totalBiayaLain += it.harga
            }

        return RekapBesar(
            totalUangMasuk,
            totalSisaBelumBayar,
            totalFeeMarketing,
            totalBiayaMarketing,
            totalBiayaLain
        )
    }

    private fun getBulanIni(
        kavlingList: List<String>,
        mapPembayaran: Map<String, List<Pembayaran>?>?,
        mapHargaKavling: Map<String, HargaKavling?>?,
        mapFeeMarketing: Map<String, FeeMarketing?>?,
        mapBiayaMarketing: Map<String, List<BiayaMarketing>?>?,
        listBiayaLain: List<BiayaLain>?,
    ): RekapBesar {
        TODO()
    }

    private fun getMingguIni(
        kavlingList: List<String>,
        mapPembayaran: Map<String, List<Pembayaran>?>?,
        mapHargaKavling: Map<String, HargaKavling?>?,
        mapFeeMarketing: Map<String, FeeMarketing?>?,
        mapBiayaMarketing: Map<String, List<BiayaMarketing>?>?,
        listBiayaLain: List<BiayaLain>?,
    ): RekapBesar {
        TODO()
    }

    private fun getCustomRekap(
        kavlingList: List<String>,
        startDate: Date?,
        endDate: Date?,
        mapPembayaran: Map<String, List<Pembayaran>?>?,
        mapHargaKavling: Map<String, HargaKavling?>?,
        mapFeeMarketing: Map<String, FeeMarketing?>?,
        mapBiayaMarketing: Map<String, List<BiayaMarketing>?>?,
        listBiayaLain: List<BiayaLain>?,
    ): RekapBesar {
        TODO()
    }

    private fun String.toDate(): Date {
        return this.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
    }

    private fun Date.toSlashedString(): String {
        val calendar = Calendar.getInstance().apply { time = this@toSlashedString }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }
}