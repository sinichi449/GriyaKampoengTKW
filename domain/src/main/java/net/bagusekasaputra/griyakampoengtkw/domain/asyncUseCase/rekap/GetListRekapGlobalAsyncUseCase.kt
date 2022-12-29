package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
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

    override fun process(request: Request): Flow<Result<List<RekapGlobal>?>> {
        return flow {
            val dataDiriBatch = dataDiriRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    emit(Result.failure(it))
                }
                .getOrNull()

            val pembayaranBatch = pembayaranRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    emit(Result.failure(it))
                }
                .getOrNull()
                ?.sortMapPembayaran()

            val hargaKavlingBatch = hargaKavlingRepository.getBatch(request.listKavling)
                .first()
                .onFailure {
                    emit(Result.failure(it))
                }
                .getOrNull()

            val listRekapGlobal = mutableListOf<RekapGlobal>().apply {
                request.listKavling.forEach { kavling ->
                    val namaCostumer = dataDiriBatch?.get(kavling)?.nama ?: "-"
                    val tanggalPembelian = pembayaranBatch?.get(kavling)
                        ?.get(0)?.tanggal ?: "-"
                    val jumlahUangMasuk = pembayaranBatch?.get(kavling)
                        ?.hitungTotalUangMasuk() ?: 0L
                    val hargaKavling = hargaKavlingBatch?.get(kavling)?.hargaDanTambahLuasan ?: 0L

                    add(RekapGlobal(
                        noKavling = kavling,
                        namaCostumer = namaCostumer,
                        tanggalPembelian = tanggalPembelian,
                        harga = hargaKavling,
                        jumlahUangMasuk = jumlahUangMasuk,
                    ))
                }
            }

            emit(Result.success(listRekapGlobal))
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