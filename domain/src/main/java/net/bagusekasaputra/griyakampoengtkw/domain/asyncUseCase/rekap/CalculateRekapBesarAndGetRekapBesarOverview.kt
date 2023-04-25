package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.util.*
import kotlin.Result

class CalculateRekapBesarAndGetRekapBesarOverview(
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
    private val rekapBesarDetailRepository: RekapBesarDetailRepository,
): AsyncUseCase<CalculateRekapBesarAndGetRekapBesarOverview.Request, RekapBesarOverview>() {

    data class Request(
        val periodeRekap: PeriodeRekap,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val listKavling: List<String> = Kavling.getGriyaKavlingList(),
        val listIncludedKavlingDataLama: List<String> = emptyList(),
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<RekapBesarOverview?>> {
        return callbackFlow {
            try {
                rekapBesarDetailRepository.delete()

                // Data Baru
                val mapListPembayaranBaru = pembayaranRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()
                val mapHargaKavlingBaru = hargaKavlingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()
                val mapFeeMarketingBaru = feeMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()
                val mapListBiayaMarketingBaru = biayaMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()

                // Data Lama
                val mapListPembayaranLama = pembayaranRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                val mapHargaKavlingLama = hargaKavlingRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                // TODO: mapFeeMarketingLama
                // TODO: mapListBiayaMarketingLama

                // No matter what kavling (old/new), biaya lain always lonely :V
                val listBiayaLain = biayaLainRepository.getAllOnline(DataMode.ONLINE).first().getOrThrow()
                    ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)

                // Init variables
                var totalUangMasuk = 0L
                var totalSisaBelumBayar = 0L
                var totalFeeMarketing = 0L
                var totalBiayaMarketing = 0L

                // DATA BARU: Calculate for each Kavling and requested Periode
                request.listKavling.forEach { kavling ->
                    val listPembayaranBaru = mapListPembayaranBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val hargaKavling = mapHargaKavlingBaru?.get(kavling)
                    val feeMarketing = mapFeeMarketingBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val listBiayaMarketing = mapListBiayaMarketingBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)


                    val totalPembayaranPerKavlingBaru = Pembayaran.hitungTotalUangMasuk(listPembayaranBaru ?: emptyList())
                    val totalSisaBelumBayarPerKavlingBaru = Pembayaran.hitungTotalSisaBelumBayar(hargaKavling ?: HargaKavling(kavling, "0", "0"), totalPembayaranPerKavlingBaru)
                    val totalBiayaMarketingPerKavling = BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing ?: emptyList())
                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavlingBaru
                    totalSisaBelumBayar += totalSisaBelumBayarPerKavlingBaru
                    totalFeeMarketing += feeMarketing?.parsedBiayaMarketer ?: 0L
                    totalBiayaMarketing += totalBiayaMarketingPerKavling


                    // Mutate the maps with filtered periode. These will useful for RekapBesarDetail.
                    mapListPembayaranBaru?.set(kavling, listPembayaranBaru)
                    mapFeeMarketingBaru?.set(kavling, feeMarketing)
                    mapListBiayaMarketingBaru?.set(kavling, listBiayaMarketing)
                }

                // DATA LAMA: Calculate for each Kavling and requested Periode
                request.listIncludedKavlingDataLama.forEach { kavlingLama ->
                    val listPembayaranRekapLama = mapListPembayaranLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val hargaKavlingLama = mapHargaKavlingLama?.get(kavlingLama)
                    // TODO: Filter periode mapFeeMarketingLama
                    // TODO: Filter periode mapListBiayaMarketingLama


                    val totalPembayaranPerKavlingLama = Pembayaran.hitungTotalUangMasuk(listPembayaranRekapLama ?: emptyList())
                    val totalSisaBelumBayarPerKavlingLama = Pembayaran.hitungTotalSisaBelumBayar(hargaKavlingLama  ?: HargaKavling(kavlingLama, "0", "0"), totalPembayaranPerKavlingLama)
                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavlingLama
                    totalSisaBelumBayar += totalSisaBelumBayarPerKavlingLama
                    // TODO: Sum totalFeeMarketing
                    // TODO: Sum totalBiayaMarketing

                    // Mutate the maps with filtered periode. These will useful for RekapBesarDetail.
                    mapListPembayaranLama?.set(kavlingLama, listPembayaranRekapLama)
                    // TODO: Mutate mapFeeMarketingLama
                    // TODO: Mutate mapListBiayaMarketingLama
                }

                // This is a lonely variable, because doesn't specifically tied to kavling :(
                val totalBiayaLain = BiayaLain.hitungTotalBiayaLain(listBiayaLain)


                rekapBesarDetailRepository.insert(
                    RekapBesarDetail(
                        mapListPembayaranRekapBaru = mapListPembayaranBaru ?: mapOf(),
                        mapFeeMarketingRekapBaru = mapFeeMarketingBaru ?: mapOf(),
                        mapListBiayaMarketingRekapBaru = mapListBiayaMarketingBaru ?: mapOf(),
                        mapListPembayaranRekapLama = mapListPembayaranLama ?: mapOf(),
                        mapFeeMarketingRekapLama = mapOf(), // TODO
                        mapListBiayaMarketingRekapLama = mapOf(), // TODO
                        listBiayaLain = listBiayaLain ?: emptyList(),
                    ),
                )

                trySendBlocking(Result.success(
                    RekapBesarOverview(
                        totalUangMasuk = totalUangMasuk,
                        totalSisaBelumBayar = totalSisaBelumBayar,
                        totalFeeMarketing = totalFeeMarketing,
                        totalBiayaMarketing = totalBiayaMarketing,
                        totalBiayaLain = totalBiayaLain,
                    )
                ))
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}