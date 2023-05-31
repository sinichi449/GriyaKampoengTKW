package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

class GetProgressKavlingAsyncUseCase(
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetProgressKavlingAsyncUseCase.Request, Map<String, ProgressKavling>>() {

    data class Request(val listKavling: List<String>): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Map<String, ProgressKavling>?>> {
        return flow {
            val mapProgress = mutableMapOf<String, ProgressKavling>()

            val tanggalSekarang = Calendar.getInstance()
            val bulanIni = tanggalSekarang.get(Calendar.MONTH) + 1
            val tahunIni = tanggalSekarang.get(Calendar.YEAR)

            request.listKavling.forEach { kavling ->
                Log.d("STATUS_PEMBAYARAN", "====================================================================================")
                Log.d("STATUS_PEMBAYARAN", "Memproses progress $kavling ...")

                val uangMasukBulanIni = pembayaranRepository.getUangMasukBulanIni(kavling,
                        bulanIni, tahunIni, DataMode.OFFLINE
                    ).getOrThrow()
                val angsuranBulanan = baselinePembayaranRepository.getAngsuran(kavling, DataMode.OFFLINE) ?: 0L

                Log.d("STATUS_PEMBAYARAN", "${kavling}: Angsuran Rp. ${NumberUtil.formatLongToString(
                    angsuranBulanan
                )}")
                Log.d("STATUS_PEMBAYARAN", "${kavling}: Uang masuk Rp. ${NumberUtil.formatLongToString(uangMasukBulanIni)}")

                val progressKavling = ProgressKavling(
                    kavling = kavling,
                    angsuranBulanan = angsuranBulanan,
                    uangMasukBulanIni = uangMasukBulanIni,
                )
                mapProgress[kavling] = progressKavling
            }

            emit(Result.success(mapProgress))
        }
    }
}