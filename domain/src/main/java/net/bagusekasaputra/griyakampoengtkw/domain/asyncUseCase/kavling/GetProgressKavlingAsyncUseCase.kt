package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetProgressKavlingAsyncUseCase(
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetProgressKavlingAsyncUseCase.Request, Map<String, ProgressKavling>>() {

    data class Request(val listKavling: List<String>): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Map<String, ProgressKavling>?>> {
        return flow {
            val mapProgress = mutableMapOf<String, ProgressKavling>()

            request.listKavling.forEach { kavling ->
                Log.d("STATUS_PEMBAYARAN", "====================================================================================")
                Log.d("STATUS_PEMBAYARAN", "Memproses progress $kavling ...")

                val uangMasukBulanIni = pembayaranRepository.getUangMasukBulanIni(kavling, DataMode.OFFLINE)
                val baselinePembayaran = baselinePembayaranRepository.get(kavling, DataMode.OFFLINE)
                    .first()
                    .getOrNull()
                val angsuranBulanan = baselinePembayaran?.jumlahUang

                Log.d("STATUS_PEMBAYARAN", "${kavling}: Angsuran Rp. ${NumberUtil.formatLongToString(angsuranBulanan ?: 0L)}")
                Log.d("STATUS_PEMBAYARAN", "${kavling}: Uang masuk Rp. ${NumberUtil.formatLongToString(uangMasukBulanIni ?: 0L)}")

                if (uangMasukBulanIni != null && angsuranBulanan != null) {
                    val progressKavling = ProgressKavling(
                        kavling = kavling,
                        angsuranBulanan = angsuranBulanan,
                        uangMasukBulanIni = uangMasukBulanIni,
                    )
                    mapProgress[kavling] = progressKavling
                } else {
                    Log.d("STATUS_PEMBAYARAN", "${kavling}: No Data")
                }
            }

            emit(Result.success(mapProgress))
        }
    }
}