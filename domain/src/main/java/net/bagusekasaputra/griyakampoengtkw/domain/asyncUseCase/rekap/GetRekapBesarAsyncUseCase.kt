package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import android.util.Log
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.util.*
import kotlin.Result

class GetRekapBesarAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
    private val rekapUangMasukRepository: RekapUangMasukRepository,
): AsyncUseCase<GetRekapBesarAsyncUseCase.Request, RekapBesar>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date? = null,
        val endDate: Date? = null,
    ): AsyncUseCase.Request

    val progressState = MutableStateFlow(ProgressState(0, "Menginisialisasi ..."))

    override fun process(request: Request): Flow<Result<RekapBesar?>> {
        return flow {
            // Clear the rekap cache
            rekapUangMasukRepository.clearAll().first()
                .onFailure {
                    emit(Result.failure(it))
                }

            progressState.update { ProgressState(18, "Menyusun tabel Pembayaran ...") }
            val mapPembayaran = pembayaranRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(36, "Menyusun tabel Harga Kavling ...") }
            val mapHargaKavling = hargaKavlingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(54, "Menyusun tabel Fee Marketing ...") }
            val mapFeeMarketing = feeMarketingRepository.getBatchOnline(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(72, "Menyusun tabel Biaya Marketing ...") }
            val mapBiayaMarketing = biayaMarketingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(90, "Menyusun tabel Biaya Lain-lain ...") }
            val listBiayaLain = biayaLainRepository.getAll(false)
                .first()
                .getOrThrow()

            val mapDataDiri = dataDiriRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            var totalUangMasuk = 0L
            var totalSisaBelumBayar = 0L
            var totalFeeMarketing = 0L
            var totalBiayaMarketing = 0L
            var totalBiayaLain = 0L


            progressState.update { ProgressState(95, "Mengevaluasi data rekap ...") }
            request.kavlingList.forEach { kavling ->
                val listPembayaran = mapPembayaran?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val feeMarketing = mapFeeMarketing?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val listBiayaMarketing = mapBiayaMarketing?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val hargaKavling = mapHargaKavling?.get(kavling)

                val uangMasukKavling = if (listPembayaran.isNullOrEmpty().not()) Pembayaran.hitungTotalUangMasuk(listPembayaran!!) else 0L
                val sisaBelumBayarKavling = if (hargaKavling != null) Pembayaran.hitungTotalSisaBelumBayar(hargaKavling, uangMasukKavling) else 0L
                val biayaMarketingKavling = if (listBiayaMarketing.isNullOrEmpty().not()) BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing!!) else 0L

                totalUangMasuk += uangMasukKavling
                totalSisaBelumBayar += sisaBelumBayarKavling
                totalFeeMarketing += feeMarketing?.parsedBiayaMarketer ?: 0L
                totalBiayaMarketing += biayaMarketingKavling

                /**
                 * Creating rekap cache for Rekap Detail View
                 */
                mapDataDiri?.get(kavling)?.let { dataDiri ->
                    if (!listPembayaran.isNullOrEmpty()) {
                        listPembayaran.let { listPembayaran ->
                            listPembayaran.forEach { pembayaran ->
                                rekapUangMasukRepository.insert(
                                    RekapUangMasuk(
                                        noKavling = kavling,
                                        namaCostumer = dataDiri.nama,
                                        tanggal = pembayaran.tanggal,
                                        jenisPembayaran = pembayaran.termin,
                                        jumlahPembayaran = pembayaran.parsedJumlahUangDibayar,
                                    )
                                ).first()
                                    .onFailure {
                                        Log.d("DEBUG_ME", "GetRekapBesarUseCase:107 onFailure : ${it.message}")
                                        emit(Result.failure(it))
                                    }
                            }
                        }
                    }
                }
            }

            listBiayaLain.filterPeriode(request.periode, request.startDate, request.endDate)
                ?.forEach {
                    totalBiayaLain += it.harga
                }

            progressState.update { ProgressState(100, "Rekap akan segera dimuat!") }

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