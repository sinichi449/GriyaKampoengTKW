package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import kotlin.Result

class GetAllRekapGlobalWithRekapBesarAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<GetAllRekapGlobalWithRekapBesarAsyncUseCase.Request, AllRekapGlobalWithRekapBesar?>() {

    data class Request(val listKavling: List<String>): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<AllRekapGlobalWithRekapBesar?>> {
        val listKavling = request.listKavling
        return flow {
            val listRekapGlobal = mutableListOf<RekapGlobal>()

            var totalUangMasuk = 0L
            var totalFeeMarketing = 0L
            // to differentiate between the total biaya marketing in SINGLE Kavling,
            // and the total biaya marketing in ALL Kavling
            var totalBiayaMarketing = 0L


            listKavling.forEach { noKavling ->
                val dataDiri = dataDiriRepository
                    .getDataDiri(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()

                val sortedPembayaran = pembayaranRepository
                    .getAllPembayaran(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()
                    ?.let {
                        Pembayaran.sortPembayaran(it)
                    }
                val tanggalPembelian = if (sortedPembayaran != null)
                    Pembayaran.getTanggalPembelian(sortedPembayaran)
                else "-"

                val hargaKavling = hargaKavlingRepository
                    .getHargaKavling(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()

                val jumlahUangMasuk = if (sortedPembayaran != null)
                    Pembayaran.hitungTotalUangMasuk(sortedPembayaran)
                else 0L
                totalUangMasuk += jumlahUangMasuk

                val feeMarketing = feeMarketingRepository
                    .getByKavlingKode(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()
                totalFeeMarketing += NumberUtil.formatStringToLong(feeMarketing?.biayaMarketer ?: "0")

                val listBiayaMarketing = biayaMarketingRepository
                    .getAllByKavlingKode(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()
                val totalBiayaMaketingPerKavling = if (listBiayaMarketing != null)
                    BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing)
                else
                    0L
                totalBiayaMarketing += totalBiayaMaketingPerKavling

                listRekapGlobal.add(
                    RekapGlobal(
                        namaCostumer = dataDiri?.nama ?: "-",
                        noKavling = noKavling,
                        tanggalPembelian = tanggalPembelian,
                        harga = hargaKavling?.hargaDanTambahLuasan ?: 0L,
                        jumlahUangMasuk = jumlahUangMasuk,
                    )
                )
            }

            val listBiayaLain = biayaLainRepository
                .getAll(offline = false)
                .first()
                .getOrThrow()
            val totalBiayaLain = BiayaLain.hitungTotalBiayaLain(listBiayaLain)


            /**
             * Result consolidation
             */
            val rekapBesar = RekapBesar(
                totalUangMasuk = totalUangMasuk,
                totalFeeMarketing = totalFeeMarketing,
                totalBiayaMarketing = totalBiayaMarketing,
                totalBiayaLain = totalBiayaLain,
            )

            emit(
                Result.success(
                    AllRekapGlobalWithRekapBesar(
                        listRekapGlobal = listRekapGlobal,
                        rekapBesar = rekapBesar,
                    )
                )
            )
        }
    }
}