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
                val mapListPembayaran = pembayaranRepository.getBatch(request.listKavling).first().getOrThrow()
                val mapHargaKavling = hargaKavlingRepository.getBatch(request.listKavling).first().getOrThrow()
                val mapFeeMarketing = feeMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()
                val mapListBiayaMarketing = biayaMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()
                val listBiayaLain = biayaLainRepository.getAllOnline(DataMode.ONLINE).first().getOrThrow()

                // Init variables
                var totalUangMasuk = 0L
                var totalSisaBelumBayar = 0L
                var totalFeeMarketing = 0L
                var totalBiayaMarketing = 0L

                // Calculate for each Kavling and requested Periode
                request.listKavling.forEach { kavling ->
                    val listPembayaran = mapListPembayaran?.get(kavling)
                        ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate) ?: emptyList()
                    val hargaKavling = mapHargaKavling?.get(kavling) ?: HargaKavling(kavling, "0", "0")
                    val feeMarketing = mapFeeMarketing?.get(kavling)
                        ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate) ?: FeeMarketing(kavling, "N/A", "0", "01/01/1970")
                    val listBiayaMarketing = mapListBiayaMarketing?.get(kavling)
                        ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate) ?: emptyList()


                    val totalPembayaranPerKavling = Pembayaran.hitungTotalUangMasuk(listPembayaran)
                    val totalSisaBelumBayarPerKavling = Pembayaran.hitungTotalSisaBelumBayar(hargaKavling, totalPembayaranPerKavling)
                    val totalBiayaMarketingPerKavling = BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing)


                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavling
                    totalSisaBelumBayar += totalSisaBelumBayarPerKavling
                    totalFeeMarketing += feeMarketing.parsedBiayaMarketer
                    totalBiayaMarketing += totalBiayaMarketingPerKavling
                }

                // This is a lonely variable, because doesn't specifically tied to kavling :(
                val totalBiayaLain = listBiayaLain.run {
                    val listAsPeriode = this?.filterPeriode(request.periodeRekap, request.startDate, request.endDate) ?: emptyList()

                    BiayaLain.hitungTotalBiayaLain(listAsPeriode)
                }


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