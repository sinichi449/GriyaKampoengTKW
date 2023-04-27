package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer.Companion.toListPembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import java.util.Date

class CalculateRekapBesarAndGetRekapBesarOverview(
    private val pembayaranRepository: PembayaranRepository,
    private val dataDiriRepository: DataDiriRepository,
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

    private val _messageProgress = MutableLiveData<String>("Menginisialisasi ...")
    val messageProgress: LiveData<String>
        get() = _messageProgress

    override fun process(request: Request): Flow<Result<RekapBesarOverview?>> {
        return callbackFlow {
            try {
                rekapBesarDetailRepository.delete()

                // Data Baru
                _messageProgress.postValue("Mendapatkan metadata Pembayaran ...")
                val mapListPembayaranBaru = pembayaranRepository.getBatchOnline(request.listKavling).first().getOrThrow()
                _messageProgress.postValue("Mendapatkan metadata Data Diri ...")
                val mapListDataDiriBaru = dataDiriRepository.getBatchOnline(request.listKavling).first().getOrThrow()
                _messageProgress.postValue("Mendapatkan metadata Harga Kavling ...")
                val mapHargaKavlingBaru = hargaKavlingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()
                _messageProgress.postValue("Mendapatkan metadata Fee Marketing ...")
                val mapFeeMarketingBaru = feeMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()
                _messageProgress.postValue("Mendapatkan metadata Biaya Marketing ...")
                val mapListBiayaMarketingBaru = biayaMarketingRepository.getBatchOnline(request.listKavling).first().getOrThrow()?.toMutableMap()

                // Data Lama
                val mapListPembayaranLama = pembayaranRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()
                val mapListDataDiriLama = dataDiriRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()
                val mapHargaKavlingLama = hargaKavlingRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                val mapFeeMarketingLama = feeMarketingRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                val mapListBiayaMarketingLama = biayaMarketingRepository.getBatchBackup(request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()


                // No matter what kavling (old/new), Biaya Lain always lonely :V
                _messageProgress.postValue("Mendapatkan metadata Biaya Lain-lain ...")
                val listBiayaLain = biayaLainRepository.getAllOnline(DataMode.ONLINE).first().getOrThrow()
                    ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)

                // Init variables
                var totalUangMasuk = 0L
                var totalSisaBelumBayar = 0L
                var totalFeeMarketing = 0L
                var totalBiayaMarketing = 0L

                // DATA BARU: Calculate for each Kavling and requested Periode
                val mMapPembayaranWithNamaCostumerBaru = mutableMapOf<String, List<PembayaranWithNamaCostumer>?>()
                request.listKavling.forEach { kavling ->
                    _messageProgress.postValue("Memproses kavling $kavling ...")
                    val listPembayaranBaru = mapListPembayaranBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val hargaKavlingBaru = mapHargaKavlingBaru?.get(kavling)
                    val feeMarketingBaru = mapFeeMarketingBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val listBiayaMarketingBaru = mapListBiayaMarketingBaru?.get(kavling)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val dataDiriBaru = mapListDataDiriBaru?.get(kavling)


                    val totalPembayaranPerKavlingBaru = Pembayaran.hitungTotalUangMasuk(listPembayaranBaru ?: emptyList())
                    val totalSisaBelumBayarPerKavlingBaru = Pembayaran.hitungTotalSisaBelumBayar(hargaKavlingBaru ?: HargaKavling(kavling, "0", "0"), totalPembayaranPerKavlingBaru)
                    val totalBiayaMarketingPerKavlingBaru = BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketingBaru ?: emptyList())

                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavlingBaru
                    totalSisaBelumBayar += totalSisaBelumBayarPerKavlingBaru
                    totalFeeMarketing += feeMarketingBaru?.parsedBiayaMarketer ?: 0L
                    totalBiayaMarketing += totalBiayaMarketingPerKavlingBaru


                    // Mutate the maps with filtered periode.
                    // These will useful for RekapBesarDetail for Filtering as per Periode
                    mMapPembayaranWithNamaCostumerBaru[kavling] = listPembayaranBaru?.toListPembayaranWithNamaCostumer(kavling, dataDiriBaru?.nama ?: "N/A")
                    mapFeeMarketingBaru?.set(kavling, feeMarketingBaru)
                    mapListBiayaMarketingBaru?.set(kavling, listBiayaMarketingBaru)
                }


                // DATA LAMA: Calculate for each Kavling and requested Periode
                _messageProgress.postValue("Mengkonsolidasi data ...")
                val mMapPembayaranWithNamaCostumerLama = mutableMapOf<String, List<PembayaranWithNamaCostumer>?>()
                request.listIncludedKavlingDataLama.forEach { kavlingLama ->
                    val listPembayaranRekapLama = mapListPembayaranLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val hargaKavlingLama = mapHargaKavlingLama?.get(kavlingLama)
                    val feeMarketingLama = mapFeeMarketingLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val listBiayaMarketingLama = mapListBiayaMarketingLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val dataDiriLama = mapListDataDiriLama?.get(kavlingLama)


                    val totalPembayaranPerKavlingLama = Pembayaran.hitungTotalUangMasuk(listPembayaranRekapLama ?: emptyList())
                    val totalSisaBelumBayarPerKavlingLama = Pembayaran.hitungTotalSisaBelumBayar(hargaKavlingLama  ?: HargaKavling(kavlingLama, "0", "0"), totalPembayaranPerKavlingLama)
                    val totalBiayaMarketingPerKavlingLama = BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketingLama ?: emptyList())

                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavlingLama
                    totalSisaBelumBayar += totalSisaBelumBayarPerKavlingLama
                    totalFeeMarketing += feeMarketingLama?.parsedBiayaMarketer ?: 0L
                    totalBiayaMarketing += totalBiayaMarketingPerKavlingLama

                    // Mutate the maps with filtered periode.
                    // These will useful for RekapBesarDetail for Filtering as per Periode
                    mMapPembayaranWithNamaCostumerLama[kavlingLama] = listPembayaranRekapLama?.toListPembayaranWithNamaCostumer(kavlingLama, dataDiriLama?.nama ?: "N/A")
                    mapFeeMarketingLama?.set(kavlingLama, feeMarketingLama)
                    mapListBiayaMarketingLama?.set(kavlingLama, listBiayaMarketingLama)
                }

                // This is a lonely variable, because doesn't specifically tied to Kavling :(
                val totalBiayaLain = BiayaLain.hitungTotalBiayaLain(listBiayaLain)


                rekapBesarDetailRepository.insert(
                    RekapBesarDetail(
                        mapListPembayaranRekapBaru = mMapPembayaranWithNamaCostumerBaru,
                        mapFeeMarketingRekapBaru = mapFeeMarketingBaru ?: mapOf(),
                        mapListBiayaMarketingRekapBaru = mapListBiayaMarketingBaru ?: mapOf(),
                        mapListPembayaranRekapLama = mMapPembayaranWithNamaCostumerLama,
                        mapFeeMarketingRekapLama = mapFeeMarketingLama ?: mapOf(),
                        mapListBiayaMarketingRekapLama = mapListBiayaMarketingLama ?: mapOf(), // TODO
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