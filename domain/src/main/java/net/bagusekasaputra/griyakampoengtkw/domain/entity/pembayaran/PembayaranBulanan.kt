package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.util.Calendar
import java.util.Date

data class PembayaranBulanan(
    val kavling: String,
    val bulan: Int, // NOT  Calendar type of Bulan
    val tahun: Int,
    val listPembayaran: List<Pembayaran>,
    val baselinePembayaran: BaselinePembayaran,
    var alokasi: Long = 0L,
) {
    val uangMasuk = Pembayaran.hitungTotalUangMasuk(listPembayaran)
    val uangMasukParsed = NumberUtil.formatLongToString(uangMasuk)

    val tunggakan: Long
        get() = baselinePembayaran.jumlahUang - uangMasuk
    val tunggakanParsed = NumberUtil.formatLongToString(tunggakan)

    val alokasiParsed = NumberUtil.formatLongToString(alokasi)

    val kelunasan: Kelunasan
        get() = if (tunggakan <= 0) {
            Kelunasan.LUNAS
        } else {
            Kelunasan.KURANG
        }

    val bulanStr = DateUtil.namaBulanShort(bulan)
    val parsedBulanTahun = "$bulanStr $tahun"
    val bulanTahunDate = "1/$bulan/$tahun".toDate()

    enum class Kelunasan(val str: String) {
        LUNAS("LUNAS"), KURANG("KURANG"), NIL("NIL")
    }

    companion object {
        fun groupPembayaranIntoBulanan(
            kavling: String,
            baselinePembayaran: BaselinePembayaran,
            pembayaranList: List<Pembayaran>,
            tanggalSekarang: Date = Calendar.getInstance().time,
        ): List<PembayaranBulanan> {
            val pembayaransSortedByBulanAngsuran = pembayaranList.sortedBy {
                it.bulanAngsuran.date
            }

            return if (pembayaransSortedByBulanAngsuran.isNotEmpty()) {
                val firstPembelianDate = pembayaransSortedByBulanAngsuran.first()
                    .bulanAngsuran.date
                val lastPembayaranDate = pembayaransSortedByBulanAngsuran.last()
                    .bulanAngsuran.date

                val fromPembelianUntilLastPembayaran = DateUtil.getListMonths(
                    firstPembelianDate, lastPembayaranDate
                )
                val pembayaranBulanans = mutableListOf<PembayaranBulanan>()

                fromPembelianUntilLastPembayaran.forEach {
                    val calendar = Calendar.getInstance().apply {
                        time = it
                    }

                    // Filter pembayaran
                    val rangeSatuBulan = DateUtil.getMonthlyRangeDate(
                        calendarMonth = calendar.get(Calendar.MONTH),
                        year = calendar.get(Calendar.YEAR)
                    )
                    val pembayaransFilterBulanIni = pembayaranList.filterPeriode(
                        periode = PeriodeRekap.CUSTOM,
                        start = rangeSatuBulan[0],
                        end = rangeSatuBulan[1],
                        filterMode = Pembayaran.FILTER_USING_BULAN_ANGSURAN,
                    )

                    val pembayaranBulanan = PembayaranBulanan(
                        kavling = kavling,
                        bulan = calendar.get(Calendar.MONTH) + 1,
                        tahun = calendar.get(Calendar.YEAR),
                        listPembayaran = pembayaransFilterBulanIni ?: emptyList(),
                        baselinePembayaran = baselinePembayaran,
                    )
                    pembayaranBulanans.add(pembayaranBulanan)
                }

                val sortedByMonthsPembayaranBulanans = sort(pembayaranBulanans)

                hitungAlokasi(sortedByMonthsPembayaranBulanans)
            } else {
                emptyList()
            }
        }

        fun getPembayaranList(pembayaranBulanans: List<PembayaranBulanan>): List<Pembayaran> {
            val pembayaranList = mutableListOf<Pembayaran>()
            pembayaranBulanans.forEach {
                pembayaranList.addAll(it.listPembayaran)
            }

            return pembayaranList
        }

        /**
         * Sort a list of [PembayaranBulanan] by [PembayaranBulanan.bulan] and [PembayaranBulanan.tahun],
         * with the day of month is set to the first date of given month.
         */
        private fun sort(listPembayaranBulanan: List<PembayaranBulanan>): List<PembayaranBulanan> {
            return listPembayaranBulanan.sortedBy {
                // Convert bulan dan tahun ke objek Date, lalu diurut pakai "time" (timeMillis)
                val date = "1/${it.bulan}/${it.tahun}".toDate()

                date.time
            }
        }

        fun hitungSemuaTunggakan(pembayaranBulanans: List<PembayaranBulanan>): Long {
            var mTotal = 0L
            pembayaranBulanans.forEach {
                mTotal += it.tunggakan
            }

            return mTotal
        }

        /**
         * Check if [PembayaranBulanan] is exist within [bulan] and [tahun].
         * Not to be confused with checking for an existance of [Pembayaran].
         */
        fun isExistPembayaranBulanan(list: List<PembayaranBulanan>, bulan: Int, tahun: Int): Boolean {
            var isExist = false

            list.forEach {
                val bulanPembayaran = it.bulan
                val tahunPembayaran = it.tahun

                if (bulanPembayaran == bulan && tahunPembayaran == tahun) {
                    isExist = true
                }
            }

            return isExist
        }

        private fun hitungAlokasi(sortedPembayaranBulanan: List<PembayaranBulanan>): List<PembayaranBulanan> {
            val newList = mutableListOf<PembayaranBulanan>()
            var alokasi = 0L
            sortedPembayaranBulanan.forEach {
                alokasi += -1 * it.tunggakan
                it.alokasi = alokasi
                newList.add(it)
            }

            val calendar = Calendar.getInstance()
            val bulanSekarang = calendar.get(Calendar.MONTH) + 1
            val tahunSekarang = calendar.get(Calendar.YEAR)
            if (!isExistPembayaranBulanan(newList, bulanSekarang, tahunSekarang)) {
                val kavling = newList[0].kavling
                val baselinePembayaran = newList[0].baselinePembayaran
                val lastAlokasi = newList.last().alokasi

                newList.add(PembayaranBulanan(
                    kavling = kavling,
                    bulan = bulanSekarang,
                    tahun = tahunSekarang,
                    listPembayaran = emptyList(),
                    baselinePembayaran = baselinePembayaran,
                    alokasi = lastAlokasi - baselinePembayaran.jumlahUang,
                ))
            }

            return newList
        }
    }
}