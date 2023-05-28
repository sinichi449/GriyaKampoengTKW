package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetListPembayaranBulananAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
    private val ambilKuitansiRepository: AmbilKuitansiRepository,
): AsyncUseCase<GetListPembayaranBulananAsyncUseCase.Request, List<PembayaranBulanan>>() {

    data class Request(val kavlingKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<PembayaranBulanan>?>> {
        return flow {
            val pembayarans = pembayaranRepository
                .getAllPembayaran(request.kavlingKode, request.dataMode)
                .first().getOrThrow()
            val baseline = baselinePembayaranRepository.get(request.kavlingKode, request.dataMode)
                .first().getOrThrow()
            val hargaKavling = hargaKavlingRepository.getHargaKavling(request.kavlingKode, request.dataMode)
                .first().getOrThrow()

            if (hargaKavling != null && pembayarans != null) {
                val maskedPembayaran = Pembayaran.maskPembayaran(pembayarans, hargaKavling,
                    onCekFotoPembayaran = { termin ->
                        val sudahIsiFotoPembayaran = fotoPembayaranRepository
                            .isFotoPembayaranExist(request.kavlingKode, termin, request.dataMode)
                            .first()
                            .getOrThrow()

                        sudahIsiFotoPembayaran
                    },
                    onCekSudahAmbilKuitansi = { kavling, termin ->
                        val ambilKuitansi = ambilKuitansiRepository.get(kavling, termin).getOrThrow()
                        val sudahAmbil = ambilKuitansi?.sudahAmbil ?: false

                        Log.d("AMBIL_KUITANSI", "Kav. $kavling $termin is " +
                                sudahAmbil.toString().uppercase())

                        sudahAmbil
                    }
                )

                val pembayaranBulanans = if (baseline != null) {
                    PembayaranBulanan.groupPembayaranIntoBulanan(
                        request.kavlingKode, baseline, maskedPembayaran
                    )
                } else {
                    emit(Result.failure(IllegalStateException("Angsuran Bulanan masih kosong. Mohon segera isi!")))

                    val defaultBaseline = BaselinePembayaran(request.kavlingKode, 0, 0L, 1)

                    PembayaranBulanan.groupPembayaranIntoBulanan(
                        request.kavlingKode, defaultBaseline, maskedPembayaran
                    )
                }

                emit(Result.success(pembayaranBulanans))
            } else {
                emit(Result.success(null))
            }
        }
    }
}