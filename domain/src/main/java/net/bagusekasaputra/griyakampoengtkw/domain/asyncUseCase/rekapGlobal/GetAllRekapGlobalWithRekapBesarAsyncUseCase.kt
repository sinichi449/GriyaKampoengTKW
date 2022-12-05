package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.Result
import kotlin.math.pow

class GetAllRekapGlobalWithRekapBesarAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<GetAllRekapGlobalWithRekapBesarAsyncUseCase.Request, AllRekapGlobalWithRekapBesar?>() {

    data class Request(val listKavling: List<String>): AsyncUseCase.Request

    private val _progressState = MutableLiveData(ProgressState(0, "Menginisialisasi ..."))
    val progressState: LiveData<ProgressState>
        get() = _progressState

    override fun process(request: Request): Flow<Result<AllRekapGlobalWithRekapBesar?>> {
        val listKavling = request.listKavling
        val totalKavling = listKavling.size

        return flow {
            val listRekapGlobal = mutableListOf<RekapGlobal>()

            var totalUangMasuk = 0L
            var totalFeeMarketing = 0L
            // to differentiate between the total biaya marketing in SINGLE Kavling,
            // and the total biaya marketing in ALL Kavling
            var totalBiayaMarketing = 0L
            var totalSisaBelumBayar = 0L

            val divScale = 2
            val multiplier = BigDecimal(10.0.pow(divScale))

            listKavling.forEachIndexed { index, noKavling ->
                val percent = BigDecimal(index.plus(1))
                    .divide(BigDecimal(totalKavling), divScale, RoundingMode.HALF_UP)
                    .multiply(multiplier)
                    .toInt()


                _progressState.postValue(ProgressState(percent, "Mendapatkan data diri kavling $noKavling ..."))
                val dataDiri = dataDiriRepository
                    .getDataDiri(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()



                _progressState.postValue(ProgressState(percent, "Mendapatkan pembayaran kavling $noKavling ..."))
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

                val jumlahUangMasuk = if (sortedPembayaran != null)
                    Pembayaran.hitungTotalUangMasuk(sortedPembayaran)
                else 0L
                totalUangMasuk += jumlahUangMasuk



                _progressState.postValue(ProgressState(percent, "Mendapatkan harga kavling $noKavling ..."))
                val hargaKavling = hargaKavlingRepository
                    .getHargaKavling(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()



                _progressState.postValue(ProgressState(percent, "Mendapatkan fee marketing kavling $noKavling ..."))
                val feeMarketing = feeMarketingRepository
                    .getByKavlingKode(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()
                totalFeeMarketing += NumberUtil.formatStringToLong(feeMarketing?.biayaMarketer ?: "0")


                _progressState.postValue(ProgressState(percent, "Mendapatkan biaya marketing kavling $noKavling ..."))
                val listBiayaMarketing = biayaMarketingRepository
                    .getAllByKavlingKode(kavlingKode = noKavling, offline = false)
                    .first()
                    .getOrThrow()
                val totalBiayaMaketingPerKavling = if (listBiayaMarketing != null)
                    BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing)
                else
                    0L
                totalBiayaMarketing += totalBiayaMaketingPerKavling


                val rekapGlobal = RekapGlobal(
                    namaCostumer = dataDiri?.nama ?: "-",
                    noKavling = noKavling,
                    tanggalPembelian = tanggalPembelian,
                    harga = hargaKavling?.hargaDanTambahLuasan ?: 0L,
                    jumlahUangMasuk = jumlahUangMasuk,
                )
                totalSisaBelumBayar += rekapGlobal.sisaPembayaran

                listRekapGlobal.add(rekapGlobal)
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
                totalSisaBelumBayar = totalSisaBelumBayar,
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