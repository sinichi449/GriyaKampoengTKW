package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PembayaranWithNamaCostumer.Companion.toListPembayaranWithNamaCostumer
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.SisaPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.firstOrThrow
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling.Companion.INDEX_BATCHABLE_BIAYA_MARKETING
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling.Companion.INDEX_BATCHABLE_DATA_DIRI
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling.Companion.INDEX_BATCHABLE_FEE_MARKETING
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling.Companion.INDEX_BATCHABLE_HARGA_KAVLING
import net.bagusekasaputra.griyakampoengtkw.domain.interfaces.BatchableWithKavling.Companion.INDEX_BATCHABLE_PEMBAYARAN
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

/**
 * Temporarily disable HargaKavling Data Lama query. And that means so too Total Sisa Belum Bayar
 * for Data Lama, because it affects the Rekap Besar's "Sisa Belum Bayar" display.
 */
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

    private val batchableWithKavlingList by lazy {
        buildList {
            add(INDEX_BATCHABLE_DATA_DIRI, dataDiriRepository)
            add(INDEX_BATCHABLE_HARGA_KAVLING, hargaKavlingRepository)
            add(INDEX_BATCHABLE_PEMBAYARAN, pembayaranRepository)
            add(INDEX_BATCHABLE_FEE_MARKETING, feeMarketingRepository)
            add(INDEX_BATCHABLE_BIAYA_MARKETING, biayaMarketingRepository)
        }
    }

    private val _messageProgress = MutableStateFlow("Menginisialisasi ...")
    val messageProgress = _messageProgress.asStateFlow()

    override fun process(request: Request): Flow<Result<RekapBesarOverview?>> {
        return flow {
            rekapBesarDetailRepository.delete().getOrThrow()

            val kavlingKodeList =
                if (!request.listKavling.isNullOrEmpty()) {
                    request.listKavling
                } else {
                    Kavling.fetchKavlingKodesNoDetail(
                        DataMode.ONLINE, blockRepository, kavlingRepository, true
                    )
                }
            val filter = RekapKavling.Filter(
                periodeRekap = request.periodeRekap,
                startDate = request.startDate,
                endDate = request.endDate,
                pembayaranFilterMode = request.pembayaranFilterMode,
            )

            val baruRekapKavling = fetchDataBaru(kavlingKodeList) { index ->
                _messageProgress.update { "Mendapatkan metadata ${getBatchableName(index)} ..." }
            }
                .filterRekap(filter)
            val lamaRekapKavling = (if (request.backupName.isNullOrEmpty()) {
                RekapKavling.EMPTY()
            } else {
                fetchDataLama(request.backupName, request.listIncludedKavlingDataLama) { index ->
                    _messageProgress.update { "Recovery metadata ${getBatchableName(index)} ..." }
                }
            })
                .filterRekap(filter)

            _messageProgress.update { "Mendapatkan metadata Biaya Lain-lain ..." }
            val biayaLainList = (biayaLainRepository.getAllOnline(DataMode.ONLINE)
                .firstOrThrow() ?: emptyList())
                .filterPeriode(
                    periode = request.periodeRekap,
                    start = request.startDate,
                    end = request.endDate,
                )


            _messageProgress.update { "Sedang menghitung rekap ..." }
            val rekapBesarDetail = RekapBesarDetail(
                pembayaranBaru = baruRekapKavling.pembayaranWithNamaCostumer,
                feeMarketingBaru = baruRekapKavling.feeMarketingMap,
                biayaMarketingBaru = baruRekapKavling.biayaMarketingMap,
                pembayaranLama = lamaRekapKavling.pembayaranWithNamaCostumer,
                feeMarketingLama = lamaRekapKavling.feeMarketingMap,
                biayaMarketingLama = lamaRekapKavling.biayaMarketingMap,
                listBiayaLain = biayaLainList,
                listSisaPembayaran = baruRekapKavling.sisaPembayaran,
            )

            rekapBesarDetailRepository.insert(rekapBesarDetail).getOrThrow()

            val totalDataBaruDanLama = baruRekapKavling.kalkulasi() + lamaRekapKavling.kalkulasi()
            val totalBiayaLain = BiayaLain.hitungTotalBiayaLain(biayaLainList)
            val rekapBesarOverview = RekapBesarOverview(
                totalUangMasuk = totalDataBaruDanLama.uangMasuk,
                totalSisaBelumBayar = totalDataBaruDanLama.sisaBelumBayar,
                totalFeeMarketing = totalDataBaruDanLama.feeMarketing,
                totalBiayaMarketing = totalDataBaruDanLama.biayaMarketing,
                totalBiayaLain = totalBiayaLain
            )

            emit(Result.success(rekapBesarOverview))
        }.catch {
            emit(Result.failure(it))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun fetchDataBaru(
        kavlingList: List<String>,
        onBatchListener: (batchIndex: Int) -> Unit,
    ): RekapKavling {
        val result = buildList {
            batchableWithKavlingList.forEachIndexed { index, repository ->
                onBatchListener(index)

                val batchMap = repository.onlineBatch(kavlingList).firstOrThrow()
                    ?: emptyMap()

                add(index, batchMap)
            }
        }

        return RekapKavling(
            kavlingList = kavlingList,
            dataDiriMap = result[INDEX_BATCHABLE_DATA_DIRI] as Map<String, DataDiri?>,
            hargaKavlingMap = result[INDEX_BATCHABLE_HARGA_KAVLING] as Map<String, HargaKavling?>,
            pembayaranMap = result[INDEX_BATCHABLE_PEMBAYARAN] as Map<String, List<Pembayaran>?>,
            feeMarketingMap = result[INDEX_BATCHABLE_FEE_MARKETING] as Map<String, FeeMarketing?>,
            biayaMarketingMap = result[INDEX_BATCHABLE_BIAYA_MARKETING] as Map<String, List<BiayaMarketing>?>,
        )
    }

    /**
     * Disable get harga kavling on Fetching Data Lama
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun fetchDataLama(
        backupName: String,
        includedKavlingList: List<String>,
        onBatchListener: (batchIndex: Int) -> Unit,
    ): RekapKavling {
        val result = buildList {
            batchableWithKavlingList.forEachIndexed { index, repository ->
                onBatchListener(index)

                val batchMap = if (index != INDEX_BATCHABLE_HARGA_KAVLING) {
                        repository
                            .fromBackupBatch(backupName, includedKavlingList)
                            .firstOrThrow()
                            ?: emptyMap()
                    } else {
                        emptyMap()
                    }

                add(index, batchMap)
            }
        }

        return RekapKavling(
            kavlingList = includedKavlingList,
            dataDiriMap = result[INDEX_BATCHABLE_DATA_DIRI] as Map<String, DataDiri?>,
            hargaKavlingMap = result[INDEX_BATCHABLE_HARGA_KAVLING] as Map<String, HargaKavling?>,
            pembayaranMap = result[INDEX_BATCHABLE_PEMBAYARAN] as Map<String, List<Pembayaran>?>,
            feeMarketingMap = result[INDEX_BATCHABLE_FEE_MARKETING] as Map<String, FeeMarketing?>,
            biayaMarketingMap = result[INDEX_BATCHABLE_BIAYA_MARKETING] as Map<String, List<BiayaMarketing>?>,
        )
    }


    private fun getBatchableName(index: Int): String {
        return when (index) {
            INDEX_BATCHABLE_PEMBAYARAN -> "Pembayaran"
            INDEX_BATCHABLE_FEE_MARKETING -> "Fee Marketing"
            INDEX_BATCHABLE_DATA_DIRI -> "Data Diri"
            INDEX_BATCHABLE_BIAYA_MARKETING -> "Biaya Marketing"
            INDEX_BATCHABLE_HARGA_KAVLING -> "Harga Kavling"
            else -> "NULL"
        }
    }

    private data class RekapKavling(
        val kavlingList: List<String>,
        val dataDiriMap: Map<String, DataDiri?>,
        val hargaKavlingMap: Map<String, HargaKavling?>,
        val pembayaranMap: Map<String, List<Pembayaran>?>,
        val feeMarketingMap: Map<String, FeeMarketing?>,
        val biayaMarketingMap: Map<String, List<BiayaMarketing>?>,
    ) {
        val pembayaranWithNamaCostumer: Map<String, List<PembayaranWithNamaCostumer>?> get() {
            return buildMapOnKavlingIteration { kavling ->
                val pembayaranList = pembayaranMap[kavling] ?: emptyList()
                val dataDiri = dataDiriMap[kavling] ?: DataDiri.EMPTY()

                pembayaranList.toListPembayaranWithNamaCostumer(
                    kavling = kavling,
                    namaCostumer = dataDiri.nama,
                )
            }
        }
        val sisaPembayaran: List<SisaPembayaran> get() {
            return buildListOnKavlingIteration { kavling ->
                val namaCustomer = (dataDiriMap[kavling] ?: DataDiri.EMPTY()).nama
                val hargaKavling = hargaKavlingMap[kavling] ?: HargaKavling.EMPTY(kavling)

                val pembayaranList = pembayaranMap[kavling] ?: emptyList()
                val totalUangMasuk = Pembayaran.hitungTotalUangMasuk(pembayaranList)

                SisaPembayaran(
                    kavling = kavling,
                    namaCostumer = namaCustomer,
                    hargaKavling = hargaKavling,
                    totalUangMasuk = totalUangMasuk,
                )
            }
        }

        data class Filter(
            val periodeRekap: PeriodeRekap,
            val startDate: Date?,
            val endDate: Date?,
            val pembayaranFilterMode: Int,
        )

        data class Kalkulasi(
            val uangMasuk: Long,
            val sisaBelumBayar: Long,
            val feeMarketing: Long,
            val biayaMarketing: Long,
        ) {
            operator fun plus(other: Kalkulasi): Kalkulasi {
                return Kalkulasi(
                    uangMasuk = uangMasuk + other.uangMasuk,
                    sisaBelumBayar = sisaBelumBayar + other.sisaBelumBayar,
                    feeMarketing = feeMarketing + other.feeMarketing,
                    biayaMarketing = biayaMarketing + other.biayaMarketing,
                )
            }
        }

        fun filterRekap(filter: Filter): RekapKavling {
            val pembayaranFiltered = buildMapOnKavlingIteration { kavling ->
                val pembayaranList = pembayaranMap[kavling] ?: emptyList()

                pembayaranList.filterPeriode(
                    periode = filter.periodeRekap,
                    start = filter.startDate,
                    end = filter.endDate,
                    filterMode = filter.pembayaranFilterMode,
                ) ?: emptyList()
            }

            val feeMarketingFiltered = buildMapOnKavlingIteration { kavling ->
                val feeMarketing = feeMarketingMap[kavling] ?: FeeMarketing.EMPTY(kavling)

                feeMarketing.filterPeriode(
                    periode = filter.periodeRekap,
                    start = filter.startDate,
                    end = filter.endDate,
                )
            }

            val biayaMarketingFiltered = buildMapOnKavlingIteration { kavling ->
                val biayaMarketingList = biayaMarketingMap[kavling] ?: emptyList()

                biayaMarketingList.filterPeriode(
                    periode = filter.periodeRekap,
                    start = filter.startDate,
                    end = filter.endDate,
                )
            }

            return copy(
                pembayaranMap = pembayaranFiltered,
                feeMarketingMap = feeMarketingFiltered,
                biayaMarketingMap = biayaMarketingFiltered,
            )
        }

        fun kalkulasi(): Kalkulasi {
            var totalUangMasuk = 0L
            var totalSisaBelumBayar = 0L
            var totalFeeMarketing = 0L
            var totalBiayaMarketing = 0L

            kavlingList.forEach { kavling ->
                val hargaKavling = hargaKavlingMap[kavling] ?: HargaKavling.EMPTY(kavling)
                val pembayaranList = pembayaranMap[kavling] ?: emptyList()
                val feeMarketing = feeMarketingMap[kavling] ?: FeeMarketing.EMPTY(kavling)
                val biayaMarketingList = biayaMarketingMap[kavling] ?: emptyList()
                val uangMasukKavling = Pembayaran.hitungTotalUangMasuk(pembayaranList)

                totalUangMasuk += uangMasukKavling
                totalSisaBelumBayar += Pembayaran.hitungTotalSisaBelumBayarWithTambahLuasan(hargaKavling, uangMasukKavling)
                totalFeeMarketing += feeMarketing.parsedBiayaMarketer
                totalBiayaMarketing += BiayaMarketing.hitungTotalBiayaMarketing(biayaMarketingList)
            }

            return Kalkulasi(
                uangMasuk = totalUangMasuk,
                sisaBelumBayar = totalSisaBelumBayar,
                feeMarketing = totalFeeMarketing,
                biayaMarketing = totalBiayaMarketing
            )
        }

        private inline fun <V> buildMapOnKavlingIteration(
            builderAction: (kavling: String) -> V,
        ): Map<String, V> {
            return buildMap {
                kavlingList.forEach { kavling ->
                    put(kavling, builderAction(kavling))
                }
            }
        }

        private inline fun <V> buildListOnKavlingIteration(
            builderAction: (kavling: String) -> V,
        ): List<V> {
            return buildList {
                kavlingList.forEach { kavling ->
                    add(builderAction(kavling))
                }
            }
        }

        companion object {
            fun EMPTY(): RekapKavling {
                return RekapKavling(
                    kavlingList = emptyList(),
                    dataDiriMap = emptyMap(),
                    hargaKavlingMap = emptyMap(),
                    pembayaranMap = emptyMap(),
                    feeMarketingMap = emptyMap(),
                    biayaMarketingMap = emptyMap(),
                )
            }
        }

    }

}