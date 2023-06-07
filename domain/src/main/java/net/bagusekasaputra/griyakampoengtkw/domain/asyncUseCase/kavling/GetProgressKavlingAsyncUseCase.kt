package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

class GetProgressKavlingAsyncUseCase(
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetProgressKavlingAsyncUseCase.Request, Map<String, ProgressKavling>>() {

    data class Request(
        val listKavling: List<String>,
        val dataMode: DataMode,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Map<String, ProgressKavling>?>> {
        return flow {
            val mapProgress = mutableMapOf<String, ProgressKavling>()

            val tanggalSekarang = Calendar.getInstance()
            val bulanIni = tanggalSekarang.get(Calendar.MONTH) + 1
            val tahunIni = tanggalSekarang.get(Calendar.YEAR)

            request.listKavling.forEach { kavling ->
                val pembayaranList = pembayaranRepository.getAllPembayaran(kavling, request.dataMode)
                    .first()
                    .onFailure { emit(Result.failure(it)) }
                    .getOrNull()
                    ?: emptyList()
                val baselinePembayaran = baselinePembayaranRepository.get(kavling, request.dataMode)
                    .first()
                    .onFailure { emit(Result.failure(it)) }
                    .getOrNull()

                val angsuranBulanan = baselinePembayaran?.jumlahUang ?: 0L
                val uangMasukBulanIni = Pembayaran.uangMasukPadaBulanDanTahunIni(
                    pembayarans = pembayaranList,
                    bulan = bulanIni,
                    tahun = tahunIni,
                    filterMode = Pembayaran.FILTER_USING_BULAN_ANGSURAN,
                )

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