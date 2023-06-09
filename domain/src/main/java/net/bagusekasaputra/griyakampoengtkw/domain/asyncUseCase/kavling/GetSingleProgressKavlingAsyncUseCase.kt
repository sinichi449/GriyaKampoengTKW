package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

/**
 * This use case should use different [PembayaranRepository]'s implementation
 * than the rest of another use case classes.
 */
class GetSingleProgressKavlingAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
): AsyncUseCase<GetSingleProgressKavlingAsyncUseCase.Request, ProgressKavling>() {

    private val tanggalSekarang = Calendar.getInstance()
    private val bulanIni = tanggalSekarang.get(Calendar.MONTH) + 1
    private val tahunIni = tanggalSekarang.get(Calendar.YEAR)

    data class Request(
        val kavling: String,
        val dataMode: DataMode = DataMode.ONLINE
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<ProgressKavling?>> {
        return flow<Result<ProgressKavling?>> {
            val pembayaranList = pembayaranRepository.getAllPembayaran(request.kavling, request.dataMode)
                .first()
                .onFailure { emit(Result.failure(it)) }
                .getOrNull()
            val baselinePembayaran = baselinePembayaranRepository.get(request.kavling, request.dataMode)
                .first()
                .onFailure { emit(Result.failure(it)) }
                .getOrNull()

            val angsuranBulanan = baselinePembayaran?.jumlahUang ?: 0L
            val uangMasukBulanIni = Pembayaran.uangMasukPadaBulanDanTahunIni(
                pembayarans = pembayaranList ?: emptyList(),
                bulan = bulanIni,
                tahun = tahunIni,
                filterMode = Pembayaran.FILTER_USING_BULAN_ANGSURAN,
            )

            val progressKavling = ProgressKavling(
                kavling = request.kavling,
                angsuranBulanan = angsuranBulanan,
                uangMasukBulanIni = uangMasukBulanIni,
            )
            emit(Result.success(progressKavling))
        }.catch {
            emit(Result.failure(it))
        }
    }
}