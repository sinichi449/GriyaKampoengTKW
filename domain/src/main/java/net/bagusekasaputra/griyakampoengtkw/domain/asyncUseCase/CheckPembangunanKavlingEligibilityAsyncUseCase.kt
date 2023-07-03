package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.getOrEmitFailure
import net.bagusekasaputra.griyakampoengtkw.domain.misc.PembayaranBelumMencukupiException
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class CheckPembangunanKavlingEligibilityAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<CheckPembangunanKavlingEligibilityAsyncUseCase.Request, Unit>() {

    data class Request(val kavling: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            val pembayaranList = pembayaranRepository.getAllPembayaran(
                kavlingKode = request.kavling,
                dataMode = DataMode.OFFLINE,
            ).first().getOrEmitFailure(this)
            val hargaKavling = hargaKavlingRepository.getHargaKavling(
                kavlingKode = request.kavling,
                dataMode = DataMode.OFFLINE,
            ).first().getOrEmitFailure(this)

            if (pembayaranList.isNullOrEmpty() || hargaKavling == null) {
                emit(Result.failure(PembayaranBelumMencukupiException("Pembayaran atau Harga Kavling tidak ditemukan!")))
            } else {
                val persentase = Pembayaran.maskPembayaran(
                    listPembayaran = pembayaranList,
                    hargaKavling = hargaKavling,
                    onCekFotoPembayaran = { false },
                    onCekSudahAmbilKuitansi = {_,_ -> false},
                ).last().presentase

                if (persentase < 50.0) {
                    emit(Result.failure(PembayaranBelumMencukupiException()))
                } else {
                    emit(Result.success(Unit))
                }
            }
        }
    }
}