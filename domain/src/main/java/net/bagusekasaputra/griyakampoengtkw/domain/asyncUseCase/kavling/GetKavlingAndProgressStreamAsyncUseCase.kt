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
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.hitungTotalUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

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

    data class Request(
        val blok: String,
        val dataMode: DataMode = DataMode.ONLINE,
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
                                .getAllPembayaran(kode, request.dataMode)
                                .first()
                                .getOrElse { t ->
                                    emit(Result.failure(t))
                                    emptyList()
                                }
                            val baseline = baselineRepository
                                .get(kode, request.dataMode)
                                .first()
                                .getOrElse { t ->
                                    emit(Result.failure(t))
                                    BaselinePembayaran.EMPTY(kode)
                                }
                            val progressKavling = ProgressKavling(
                                kavling = kode,
                                angsuranBulanan = baseline?.jumlahUang ?: 0L,
                                uangMasukBulanIni = hitungTotalUangMasuk(pembayaranList ?: emptyList())
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