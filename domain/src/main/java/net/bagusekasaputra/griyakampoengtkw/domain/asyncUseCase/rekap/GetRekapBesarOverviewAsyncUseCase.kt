package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer.Companion.toListPembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.SisaPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import java.util.Date

class GetRekapBesarOverviewAsyncUseCase(
    private val blockRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
    private val rekapBesarDetailRepository: RekapBesarDetailRepository,
): AsyncUseCase<GetRekapBesarOverviewAsyncUseCase.Request, RekapBesarOverview>() {

    data class Request(
        val periodeRekap: PeriodeRekap,
        val startDate: Date? = null,
        val endDate: Date? = null,
        // If listKavling is null, then it assumes all kavling available
        val listKavling: List<String>?,
        val backupName: String? = null,
        val listIncludedKavlingDataLama: List<String> = emptyList(),
        val pembayaranFilterMode: Int = Pembayaran.FILTER_USING_TANGGAL,
    ): AsyncUseCase.Request

//    private val _messageProgress = MutableLiveData("Menginisialisasi ...")
//    val messageProgress: LiveData<String>
//        get() = _messageProgress


    /**
     * Temporarily disable HargaKavling Data Lama query. And that means so too Total Sisa Belum Bayar
     * for Data Lama, because it affects the Rekap Besar's "Sisa Belum Bayar" display.
     */
    override fun process(request: Request): Flow<Result<RekapBesarOverview?>> {
        return callbackFlow {
            try {
                rekapBesarDetailRepository.delete()

//                _messageProgress.postValue("Mendapatkan Blok dan Kavling ...")
                val kavlingKodeList =
                    if (!request.listKavling.isNullOrEmpty()) {
                        request.listKavling
                    } else {
                        val dataMode = if (request.backupName.isNullOrEmpty())
                            DataMode.ONLINE else DataMode.DATA_LAMA

                        Kavling.fetchKavlingKodesNoDetail(
                            dataMode, blockRepository, kavlingRepository, true
                        )
                    }

                // Data Baru
//                _messageProgress.postValue("Mendapatkan metadata Pembayaran ...")
                val mapListPembayaranBaru = pembayaranRepository.getBatchOnline(kavlingKodeList).first().getOrThrow()
//                _messageProgress.postValue("Mendapatkan metadata Data Diri ...")
                val mapListDataDiriBaru = dataDiriRepository.getBatchOnline(kavlingKodeList).first().getOrThrow()
//                _messageProgress.postValue("Mendapatkan metadata Harga Kavling ...")
                val mapHargaKavlingBaru = hargaKavlingRepository.getBatchOnline(kavlingKodeList).first().getOrThrow()?.toMutableMap()
//                _messageProgress.postValue("Mendapatkan metadata Fee Marketing ...")
                val mapFeeMarketingBaru = feeMarketingRepository.getBatchOnline(kavlingKodeList).first().getOrThrow()?.toMutableMap()
//                _messageProgress.postValue("Mendapatkan metadata Biaya Marketing ...")
                val mapListBiayaMarketingBaru = biayaMarketingRepository.getBatchOnline(kavlingKodeList).first().getOrThrow()?.toMutableMap()

                // Data Lama
                val mapListPembayaranLama: Map<String, List<Pembayaran>?>?
                val mapListDataDiriLama: Map<String, DataDiri?>?
//                val mapHargaKavlingLama: MutableMap<String, HargaKavling?>?
                val mapFeeMarketingLama: MutableMap<String, FeeMarketing?>?
                val mapListBiayaMarketingLama: MutableMap<String, List<BiayaMarketing>?>?
                if (request.backupName != null) {
                    mapListPembayaranLama = pembayaranRepository.getBatchFromRemoteBackup(request.backupName, request.listIncludedKavlingDataLama).first().getOrThrow()
                    mapListDataDiriLama = dataDiriRepository.getBatchFromRemoteBackup(request.backupName, request.listIncludedKavlingDataLama).first().getOrThrow()
//                    mapHargaKavlingLama = hargaKavlingRepository.getBatchFromRemoteBackup(request.backupName, request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                    mapFeeMarketingLama = feeMarketingRepository.getBatchFromRemoteBackup(request.backupName, request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                    mapListBiayaMarketingLama = biayaMarketingRepository.getBatchFromRemoteBackup(request.backupName, request.listIncludedKavlingDataLama).first().getOrThrow()?.toMutableMap()
                } else {
                    mapListPembayaranLama = null
                    mapListDataDiriLama = null
//                    mapHargaKavlingLama = null
                    mapFeeMarketingLama = null
                    mapListBiayaMarketingLama = null
                }


                // No matter what kavling (old/new), Biaya Lain always lonely :V
//                _messageProgress.postValue("Mendapatkan metadata Biaya Lain-lain ...")
                val listBiayaLain = biayaLainRepository.getAllOnline(DataMode.ONLINE).first().getOrThrow()
                    ?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)

                // Init variables
                var totalUangMasuk = 0L
                var totalSisaBelumBayar = 0L
                var totalFeeMarketing = 0L
                var totalBiayaMarketing = 0L

                // DATA BARU: Calculate for each Kavling and requested Periode
                // DATA BARU: Sisa Pembayaran only in Data Baru
                val mMapPembayaranWithNamaCostumerBaru = mutableMapOf<String, List<PembayaranWithNamaCostumer>?>()
                val listSisaPembayaran = mutableListOf<SisaPembayaran>()
                kavlingKodeList.forEach { kavling ->
//                    _messageProgress.postValue("Memproses kavling $kavling ...")
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

                    if (!listPembayaranBaru.isNullOrEmpty()) {
                        listSisaPembayaran.add(SisaPembayaran(
                            kavling = kavling,
                            namaCostumer = dataDiriBaru?.nama ?: "N/A",
                            hargaKavling = hargaKavlingBaru ?: HargaKavling(kavling, "0", "0"),
                            totalUangMasuk = totalPembayaranPerKavlingBaru,
                        ))
                    }
                }


                // DATA LAMA: Calculate for each Kavling and requested Periode
//                _messageProgress.postValue("Mengkonsolidasi data ...")
                val mMapPembayaranWithNamaCostumerLama = mutableMapOf<String, List<PembayaranWithNamaCostumer>?>()
                request.listIncludedKavlingDataLama.forEach { kavlingLama ->
                    val listPembayaranRekapLama = mapListPembayaranLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
//                    val hargaKavlingLama = mapHargaKavlingLama?.get(kavlingLama)
                    val feeMarketingLama = mapFeeMarketingLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val listBiayaMarketingLama = mapListBiayaMarketingLama?.get(kavlingLama)?.filterPeriode(request.periodeRekap, request.startDate, request.endDate)
                    val dataDiriLama = mapListDataDiriLama?.get(kavlingLama)


                    val totalPembayaranPerKavlingLama = Pembayaran.hitungTotalUangMasuk(listPembayaranRekapLama ?: emptyList())

//                    val totalSisaBelumBayarPerKavlingLama = Pembayaran.hitungTotalSisaBelumBayar(hargaKavlingLama  ?: HargaKavling(kavlingLama, "0", "0"), totalPembayaranPerKavlingLama)
                    val totalBiayaMarketingPerKavlingLama = BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketingLama ?: emptyList())

                    // Sum it UP!
                    totalUangMasuk += totalPembayaranPerKavlingLama
//                    totalSisaBelumBayar += totalSisaBelumBayarPerKavlingLama
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
                        listSisaPembayaran = listSisaPembayaran,
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