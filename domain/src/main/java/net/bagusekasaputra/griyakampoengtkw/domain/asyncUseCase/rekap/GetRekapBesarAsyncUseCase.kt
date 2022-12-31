package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import android.util.Log
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.*
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.util.*
import kotlin.Result

class GetRekapBesarAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
    private val rekapUangMasukRepository: RekapUangMasukRepository,
): AsyncUseCase<GetRekapBesarAsyncUseCase.Request, RekapBesar>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date? = null,
        val endDate: Date? = null,
    ): AsyncUseCase.Request

    val progressState = MutableStateFlow(ProgressState(0, "Menginisialisasi ..."))

    override fun process(request: Request): Flow<Result<RekapBesar?>> {
        return flow {
            // Clear the rekap cache
            rekapUangMasukRepository.clearAll().first()
                .onFailure {
                    emit(Result.failure(it))
                }

            progressState.update { ProgressState(18, "Menyusun tabel Pembayaran ...") }
            val mapPembayaran = pembayaranRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(36, "Menyusun tabel Harga Kavling ...") }
            val mapHargaKavling = hargaKavlingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(54, "Menyusun tabel Fee Marketing ...") }
            val mapFeeMarketing = feeMarketingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(72, "Menyusun tabel Biaya Marketing ...") }
            val mapBiayaMarketing = biayaMarketingRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            progressState.update { ProgressState(90, "Menyusun tabel Biaya Lain-lain ...") }
            val listBiayaLain = biayaLainRepository.getAll(false)
                .first()
                .getOrThrow()

            val mapDataDiri = dataDiriRepository.getBatch(request.kavlingList)
                .first()
                .getOrThrow()

            var totalUangMasuk = 0L
            var totalSisaBelumBayar = 0L
            var totalFeeMarketing = 0L
            var totalBiayaMarketing = 0L
            var totalBiayaLain = 0L


            progressState.update { ProgressState(95, "Mengevaluasi data rekap ...") }
            request.kavlingList.forEach { kavling ->
                val listPembayaran = mapPembayaran?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val feeMarketing = mapFeeMarketing?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val listBiayaMarketing = mapBiayaMarketing?.get(kavling)
                    .filterPeriode(request.periode, request.startDate, request.endDate)
                val hargaKavling = mapHargaKavling?.get(kavling)

                val uangMasukKavling = if (listPembayaran.isNullOrEmpty().not()) Pembayaran.hitungTotalUangMasuk(listPembayaran!!) else 0L
                val sisaBelumBayarKavling = if (hargaKavling != null) Pembayaran.hitungTotalSisaBelumBayar(hargaKavling, uangMasukKavling) else 0L
                val biayaMarketingKavling = if (listBiayaMarketing.isNullOrEmpty().not()) BiayaMarketing.hitungTotalBiayaMarketing(listBiayaMarketing!!) else 0L

                totalUangMasuk += uangMasukKavling
                totalSisaBelumBayar += sisaBelumBayarKavling
                totalFeeMarketing += feeMarketing?.parsedBiayaMarketer ?: 0L
                totalBiayaMarketing += biayaMarketingKavling

                /**
                 * Creating rekap cache for Rekap Detail View
                 */
                mapDataDiri?.get(kavling)?.let { dataDiri ->
                    if (!listPembayaran.isNullOrEmpty()) {
                        listPembayaran.let { listPembayaran ->
                            listPembayaran.forEach { pembayaran ->
                                rekapUangMasukRepository.insert(
                                    RekapUangMasuk(
                                        noKavling = kavling,
                                        namaCostumer = dataDiri.nama,
                                        tanggal = pembayaran.tanggal,
                                        jenisPembayaran = pembayaran.termin,
                                        jumlahPembayaran = pembayaran.parsedJumlahUangDibayar,
                                    )
                                ).first()
                                    .onFailure {
                                        Log.d("DEBUG_ME", "GetRekapBesarUseCase:107 onFailure : ${it.message}")
                                        emit(Result.failure(it))
                                    }
                            }
                        }
                    }
                }
            }

            listBiayaLain.filterPeriode(request.periode, request.startDate, request.endDate)
                ?.forEach {
                    totalBiayaLain += it.harga
                }

            progressState.update { ProgressState(100, "Rekap akan segera dimuat!") }

            emit(Result.success(RekapBesar(
                totalUangMasuk,
                totalSisaBelumBayar,
                totalFeeMarketing,
                totalBiayaMarketing,
                totalBiayaLain
            )))
        }
    }

    private fun List<Pembayaran>?.filterPeriode(
        periode: PeriodeRekap,
        start: Date?,
        end: Date?,
    ): List<Pembayaran>? {
        return when(periode) {
            PeriodeRekap.SEMUA -> this
            PeriodeRekap.TAHUN_INI ->  {
                this?.filter { pembayaran ->
                    val tahunPembayaran = Calendar.getInstance().let {
                        it.time = pembayaran.tanggal.toDate()
                        it.get(Calendar.YEAR)
                    }

                    tahunPembayaran == getTahunSekarang()
                }
            }
            PeriodeRekap.BULAN_INI -> {
                val rangeTanggal = getMonthlyRangeDate()
                val tanggalPertama = rangeTanggal[0]
                val tanggalTerakhir = rangeTanggal[1]

                this?.filter { pembayaran ->
                    val tanggalPembayaran = pembayaran.tanggal.toDate()

                    tanggalPembayaran.isWithinRange(tanggalPertama, tanggalTerakhir)
                }
            }
            PeriodeRekap.MINGGU_INI -> {
                // Get first and end date of the week, which is get
                // the date of Sunday and the next Sunday
                val rangeTanggal = getWeeklyRangeDate()
                val startDate = rangeTanggal[0]
                val endDate = rangeTanggal[1]

                this?.filter { pembayaran ->
                    val tanggalPembayaran = pembayaran.tanggal.toDate()

                    tanggalPembayaran.isWithinRange(startDate, endDate)
                }

            }
            PeriodeRekap.CUSTOM -> {
                val rangeTanggal = getCustomRangeDate(start!!, end!!)
                val startDate = rangeTanggal[0]
                val endDate = rangeTanggal[1]

                this?.filter {
                    val tanggalPembayaran = it.tanggal.toDate()

                    tanggalPembayaran.isWithinRange(startDate, endDate)
                }
            }
        }
    }

    private fun FeeMarketing?.filterPeriode(
        periode: PeriodeRekap,
        start: Date?,
        end: Date?,
    ): FeeMarketing? {
        return this?.let {
            val tanggalPenerimaan = it.tanggalPenerimaan.toDate()

            when(periode) {
                PeriodeRekap.SEMUA -> it
                PeriodeRekap.TAHUN_INI -> {
                    val tahunPenerimaan = Calendar.getInstance().run {
                        time = tanggalPenerimaan

                        get(Calendar.YEAR)
                    }

                    if (getTahunSekarang() == tahunPenerimaan) it else null
                }
                PeriodeRekap.BULAN_INI -> {
                    val rangeTanggal = getMonthlyRangeDate()
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                }
                PeriodeRekap.MINGGU_INI -> {
                    val rangeTanggal = getWeeklyRangeDate()
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                }
                PeriodeRekap.CUSTOM -> {
                    val rangeTanggal = getCustomRangeDate(start!!, end!!)
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    if (tanggalPenerimaan.isWithinRange(startDate, endDate)) it else null
                }
            }
        }
    }

    @JvmName("filterPeriodeBiayaMarketing")
    private fun List<BiayaMarketing>?.filterPeriode(
        periode: PeriodeRekap,
        start: Date?,
        end: Date?,
    ): List<BiayaMarketing>? {
        return when (periode) {
            PeriodeRekap.SEMUA -> this
            PeriodeRekap.TAHUN_INI -> this?.filter {
                val tahunBiayaMarketing = Calendar.getInstance().run {
                    time = it.tanggal.toDate()

                    get(Calendar.YEAR)
                }

                tahunBiayaMarketing == getTahunSekarang()
            }
            PeriodeRekap.BULAN_INI -> this?.filter {
                val rangeBulan = getMonthlyRangeDate()
                val startDate = rangeBulan[0]
                val endDate = rangeBulan[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
            PeriodeRekap.MINGGU_INI -> this?.filter {
                val rangeMinggu = getWeeklyRangeDate()
                val startDate = rangeMinggu[0]
                val endDate = rangeMinggu[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
            PeriodeRekap.CUSTOM -> this?.filter {
                val rangeTanggal = getCustomRangeDate(start!!, end!!)
                val startDate = rangeTanggal[0]
                val endDate = rangeTanggal[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
        }
    }

    @JvmName("filterPeriodeBiayaLain")
    private fun List<BiayaLain>?.filterPeriode(
        periode: PeriodeRekap,
        start: Date?,
        end: Date?,
    ): List<BiayaLain>? {
        return when (periode) {
            PeriodeRekap.SEMUA -> this
            PeriodeRekap.TAHUN_INI -> this?.filter {
                val tahunBiayaLain = Calendar.getInstance().run {
                    time = it.tanggal.toDate()

                    get(Calendar.YEAR)
                }

                getTahunSekarang() == tahunBiayaLain
            }
            PeriodeRekap.BULAN_INI -> this?.filter {
                val rangeBulan = getMonthlyRangeDate()
                val startDate = rangeBulan[0]
                val endDate = rangeBulan[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
            PeriodeRekap.MINGGU_INI -> this?.filter {
                val rangeMinggu = getWeeklyRangeDate()
                val startDate = rangeMinggu[0]
                val endDate = rangeMinggu[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
            PeriodeRekap.CUSTOM -> this?.filter {
                val rangeTanggal = getCustomRangeDate(start!!, end!!)
                val startDate = rangeTanggal[0]
                val endDate = rangeTanggal[1]

                it.tanggal.toDate().isWithinRange(startDate, endDate)
            }
        }
    }

    fun getTahunSekarang() = Calendar.getInstance().get(Calendar.YEAR)

    fun getMonthlyRangeDate(): List<Date> {
        // Get first and end of day in current month
        val tanggalPertama = Calendar.getInstance().apply {
            // Set ke tanggal 1 bulan sekarang
            set(Calendar.DAY_OF_MONTH, 1)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val tanggalTerakhir = Calendar.getInstance().apply {
            // Set ke tanggal terakhir bulan sekarang (otomatis mengikuti bulan)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DATE))

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return listOf(tanggalPertama, tanggalTerakhir)
    }

    fun getWeeklyRangeDate(): List<Date> {
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance().apply {
            time = startDate.time

            add(Calendar.DAY_OF_WEEK, 7)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return listOf(startDate.time, endDate.time)
    }

    fun getCustomRangeDate(start: Date, end: Date): List<Date> {
        val startDate = Calendar.getInstance().apply {
            time = start

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance().apply {
            time = end

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return listOf(startDate.time, endDate.time)
    }

    private fun String.toDate(): Date {
        return this.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
    }

    private fun Date.toSlashedString(): String {
        val calendar = Calendar.getInstance().apply { time = this@toSlashedString }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }

    private fun Date.isWithinRange(startDate: Date, endDate: Date)
        = !(this.before(startDate) || this.after(endDate))
}