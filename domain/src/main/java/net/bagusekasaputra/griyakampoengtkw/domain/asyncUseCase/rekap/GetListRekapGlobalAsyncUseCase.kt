package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetListRekapGlobalAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<GetListRekapGlobalAsyncUseCase.Request, List<RekapGlobal>?>() {

    data class Request(val listKavling: List<String>): AsyncUseCase.Request

    val progressState = MutableStateFlow(ProgressState(0, "Menginisialisasi"))

    override fun process(request: Request): Flow<Result<List<RekapGlobal>?>> {
        return callbackFlow {
            Log.d("DEBUG_ME", "RekapGlobalUseCase: Getting data diri ...")
            progressState.update { ProgressState(25, "Menyusun tabel Data Diri ...") }
            val dataDiriBatch = dataDiriRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:32 onFailure -> ${it.message}")))
                }
                .getOrNull()

            Log.d("DEBUG_ME", "RekapGlobalUseCase: Getting tabel pembayaran ...")
            progressState.update { ProgressState(50, "Menyusun tabel Pembayaran ...") }
            val pembayaranBatch = pembayaranRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:40 onFailure -> ${it.message}")))
                }
                .getOrNull()
                ?.sortMapPembayaran()

            Log.d("DEBUG_ME", "RekapGlobalUseCase: Getting harga kavling ...")
            progressState.update { ProgressState(75, "Menyusun tabel Harga Kavling ...") }
            val hargaKavlingBatch = hargaKavlingRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:49 onFailure -> ${it.message}")))
                }
                .getOrNull()

            Log.d("DEBUG_ME", "RekapGlobalUseCase: Consolidating list ...")
            progressState.update { ProgressState(95, "Mengevaluasi rekap global ...") }

            val listRekapGlobal = mutableListOf<RekapGlobal>()
            request.listKavling.forEach { kavling ->
                Log.d("DEBUG_ME", "RekapGlobalUseCase: Evaluating $kavling now!")

                val namaCostumer = dataDiriBatch?.get(kavling)?.nama ?: "-"
                val tanggalPembelian = pembayaranBatch?.get(kavling).let {
                    if (it.isNullOrEmpty().not()) Pembayaran.getTanggalPembelian(it!!)
                    else "-"
                }
                val hargaKavling = hargaKavlingBatch?.get(kavling)?.hargaDanTambahLuasan ?: 0L
                val jumlahUangMasuk = pembayaranBatch?.get(kavling).let {
                    if (it.isNullOrEmpty().not()) Pembayaran.hitungTotalUangMasuk(it!!)
                    else 0L
                }

                listRekapGlobal.add(RekapGlobal(
                    noKavling = kavling,
                    namaCostumer = namaCostumer,
                    tanggalPembelian = tanggalPembelian,
                    harga = hargaKavling,
                    jumlahUangMasuk = jumlahUangMasuk,
                ))
            }

            trySendBlocking(Result.success(listRekapGlobal))

            awaitClose {  }
        }
    }



    private fun Map<String, List<Pembayaran>?>?.sortMapPembayaran(): Map<String, List<Pembayaran>?>? {
        return if (this != null) {
            val newMap = mutableMapOf<String, List<Pembayaran>?>()

            this.keys.forEach { kavling ->
                val listPembayaran = this[kavling]
                if (listPembayaran != null) {
                    newMap[kavling] = PembayaranSorterUtil(listPembayaran).getSortedList()
                } else {
                    newMap[kavling] = null
                }
            }

            newMap.toMap()
        } else {
            null
        }
    }

    private fun List<Pembayaran>?.hitungTotalUangMasuk(): Long? {
        return if (this != null) {
            Pembayaran.hitungTotalUangMasuk(this)
        } else {
            null
        }
    }
}