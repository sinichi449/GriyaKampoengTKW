package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.math.BigDecimal
import java.math.RoundingMode

class GetAllRekapGlobalUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<GetAllRekapGlobalUseCase.Request, List<RekapGlobal>?>() {

    data class Request(val kavlingList: List<String>): AsyncUseCase.Request

    private val _progressState = MutableLiveData<ProgressState>(
        ProgressState(0, "Menginisialisasi ...")
    )
    val progressState: LiveData<ProgressState>
        get() = _progressState


    override fun process(request: Request): Flow<Result<List<RekapGlobal>?>> {
        return flow {
            val kavlingList = request.kavlingList
            val listRekapGlobal = mutableListOf<RekapGlobal>()

            kavlingList.forEachIndexed { index, kavlingKode ->
                val percentProgress = BigDecimal(index.plus(1))
                    .divide(BigDecimal(kavlingList.size), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
                    .toInt()

                _progressState.postValue(ProgressState(percent = percentProgress, message = "Memuat data diri kavling $kavlingKode ..."))
                val dataDiri = dataDiriRepository.getDataDiri(kavlingKode, false)
                    .first()
                    .getOrThrow()

                _progressState.postValue(ProgressState(percent = percentProgress, message = "Memuat pembayaran kavling $kavlingKode ..."))
                val listPembayaran = pembayaranRepository.getAllPembayaran(kavlingKode, false)
                    .first()
                    .getOrThrow()
                val sortedPembayaran = if (listPembayaran != null)
                        PembayaranSorterUtil(listPembayaran).getSortedList()
                    else
                        null

                val hargaKavling = hargaKavlingRepository.getHargaKavling(kavlingKode, false)
                    .first()
                    .getOrThrow()

                listRekapGlobal.add(
                    RekapGlobal(
                        namaCostumer = dataDiri?.nama ?: "-",
                        noKavling = kavlingKode,
                        tanggalPembelian = getTanggalPembelian(sortedPembayaran),
                        harga = hargaKavling?.hargaDanTambahLuasan ?: 0L,
                        jumlahUangMasuk = if (sortedPembayaran != null) Pembayaran.hitungTotalUangMasuk(sortedPembayaran) else 0L,
                    )
                )
            }

            emit(Result.success(listRekapGlobal))
        }
    }

    private fun getTanggalPembelian(sortedListPembayaran: List<Pembayaran>?): String {
        return if (sortedListPembayaran != null) {
            if (sortedListPembayaran.isNotEmpty()) {
               sortedListPembayaran.first().tanggal
            } else {
                "-"
            }
        } else {
            "-"
        }
    }
}