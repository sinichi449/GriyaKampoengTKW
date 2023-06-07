package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetListRekapGlobalAsyncUseCase(
    private val blockRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<GetListRekapGlobalAsyncUseCase.Request, List<RekapGlobal>?>() {

    data class Request(
        // If null, then it assumes to fetch all kavling
        val listKavling: List<String>?
    ): AsyncUseCase.Request

    val progressState = MutableStateFlow(ProgressState(0, "Menginisialisasi"))

    override fun process(request: Request): Flow<Result<List<RekapGlobal>?>> {
        return callbackFlow {
            progressState.update { ProgressState(1, "Menyusun tabel Blok dan Kavling ...") }
            val kavlingKodeList =
                if (!request.listKavling.isNullOrEmpty()) request.listKavling
                else Kavling.fetchKavlingKodesNoDetail(DataMode.ONLINE, blockRepository, kavlingRepository, true)

            progressState.update { ProgressState(25, "Menyusun tabel Data Diri ...") }
            val dataDiriBatch = dataDiriRepository.onlineBatch(kavlingKodeList)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:32 onFailure -> ${it.message}")))
                }
                .getOrNull()

            progressState.update { ProgressState(50, "Menyusun tabel Pembayaran ...") }
            val pembayaranBatch = pembayaranRepository.onlineBatch(kavlingKodeList)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:40 onFailure -> ${it.message}")))
                }
                .getOrNull()

            progressState.update { ProgressState(75, "Menyusun tabel Harga Kavling ...") }
            val hargaKavlingBatch = hargaKavlingRepository.onlineBatch(kavlingKodeList)
                .first()
                .onFailure {
                    trySendBlocking(Result.failure(Exception("GetListRekapGlobalUseCase:49 onFailure -> ${it.message}")))
                }
                .getOrNull()

            progressState.update { ProgressState(95, "Mengevaluasi rekap global ...") }

            val listRekapGlobal = mutableListOf<RekapGlobal>()
            kavlingKodeList.forEach { kavling ->
                val namaCostumer = dataDiriBatch?.get(kavling)?.nama ?: "-"
                val tanggalPembelian = pembayaranBatch?.get(kavling).let {
                    if (it.isNullOrEmpty().not()) Pembayaran.getTanggalPembelian(it!!)
                    else null
                }
                val hargaKavling = hargaKavlingBatch?.get(kavling)?.hargaDanTambahLuasan ?: 0L
                val jumlahUangMasuk = pembayaranBatch?.get(kavling).let {
                    if (it.isNullOrEmpty().not()) Pembayaran.hitungTotalUangMasuk(it!!)
                    else 0L
                }

                listRekapGlobal.add(
                    RekapGlobal(
                    noKavling = kavling,
                    namaCostumer = namaCostumer,
                    tanggalPembelian = tanggalPembelian?.toDate(),
                    harga = hargaKavling,
                    jumlahUangMasuk = jumlahUangMasuk,
                )
                )
            }

            trySendBlocking(Result.success(listRekapGlobal))

            awaitClose {  }
        }
    }

}