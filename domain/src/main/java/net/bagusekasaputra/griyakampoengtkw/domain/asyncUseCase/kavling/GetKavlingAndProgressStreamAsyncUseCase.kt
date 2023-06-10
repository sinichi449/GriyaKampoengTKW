package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress.Companion.sortByKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

/**
 * Getting continuous streams of [KavlingAndProgress]'s lists.
 *
 * Don't use [Flow.first] to get the data, as it will only return only ONE list of [KavlingAndProgress].
 *
 * Always use [Flow.collect], and update your UI with replacing old list within [Flow.collect] lambda.
 *
 * Note that this use case should NOT use the Legacy [PembayaranRepository] implementation.
 */
class GetKavlingAndProgressStreamAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val baselineRepository: BaselinePembayaranRepository,
): AsyncUseCase<GetKavlingAndProgressStreamAsyncUseCase.Request, List<KavlingAndProgress>>() {

    private val calendar = Calendar.getInstance()

    /**
     * [AsyncUseCase.Request] for this use case.
     *
     * @param blok Specify which [blok] you want to fetch.
     * @param dataMode Currently only supports [DataMode.ONLINE]. If you pass another [DataMode],
     * it will get ignored.
     * @param bulanAngsuran Which month does the [Pembayaran] will be calculated into [ProgressKavling.uangMasukBulanIni]?
     * If left null, it will use current actual MONTH. Useful for testing purpose.
     * @param tahunAngsuran Which year does the [Pembayaran] will be calculated into [ProgressKavling.uangMasukBulanIni]?
     * If left null, it will use current actual YEAR. Useful for testing purpose.
     */

    data class Request(
        val blok: String,
        val dataMode: DataMode = DataMode.ONLINE,
        val bulanAngsuran: Int? = null,
        val tahunAngsuran: Int? = null,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<KavlingAndProgress>?>> {
        return flow {
            val unsortedResults = mutableListOf<KavlingAndProgress>()

            kavlingRepository.getAsFlow(request.blok)
                .catch { emit(Result.failure(it)) }
                .collect { result ->
                    result.onFailure {
                        emit(Result.failure(it))
                    }
                    result.onSuccess {
                        it?.also { kavling ->
                            val kode = kavling.kode
                            val pembayaranList = pembayaranRepository
                                .getAllPembayaran(kode, DataMode.ONLINE)
                                .first()
                                .getOrElse { t ->
                                    emit(Result.failure(t))
                                    emptyList()
                                }
                            val baseline = baselineRepository
                                .get(kode, DataMode.ONLINE)
                                .first()
                                .getOrElse { t ->
                                    emit(Result.failure(t))
                                    BaselinePembayaran.EMPTY(kode)
                                }


                            val filterBulan = request.bulanAngsuran ?: (calendar.get(Calendar.MONTH) + 1)
                            val filterTahun = request.tahunAngsuran ?: (calendar.get(Calendar.YEAR))
                            val uangMasukBulanIni = Pembayaran.uangMasukPadaBulanDanTahunIni(
                                pembayarans = pembayaranList ?: emptyList(),
                                bulan = filterBulan,
                                tahun = filterTahun,
                                filterMode = Pembayaran.FILTER_USING_BULAN_ANGSURAN,
                            )

                            val progressKavling = ProgressKavling(
                                kavling = kode,
                                angsuranBulanan = baseline?.jumlahUang ?: 0L,
                                uangMasukBulanIni = uangMasukBulanIni,
                            )

                            val kavlingAndProgress = KavlingAndProgress(
                                blok = request.blok,
                                kavling = kavling,
                                progress = progressKavling,
                            )
                            unsortedResults.add(kavlingAndProgress)

                            emit(Result.success(unsortedResults.sortByKavling(request.blok)))
                        }
                    }
                }
        }
    }

}