package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getCustomRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.getWeeklyRangeDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.isWithinRange
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.JenisTermin
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date

/**
 * A class for representing Pembayaran. You must pass these essential arguments: [termin],
 * [tanggal], [jumlahUangDibayar], [keterangan], and [timeMillis].
 *
 * @param termin must be a combination of [JenisTermin] and an integer. E.g: **ITJ 1**, **DP 3**,
 * **Termin 20**, etc. Note that between [JenisTermin] and the integer, there should be a **whitespace**.
 * Don't pass this kind of argument: _ITJ1_, _DP3_, _Termin20_, etc.
 * @param tanggal a slashed-style date to show when the [Pembayaran] occurred,
 * for example: _17/12/2023_ would means December, 17th 2023. Highly recommended to pad the Integer < 10 with **0**. So, to represent June, 9th 2023 would be
 * _09/06/2023_.
 * @param jumlahUangDibayar a comma separated [String] represents the amount of [Pembayaran].
 * For example: _1,000,000_, 3,500,000_, etc.
 * @param bulanAngsuran specify [BulanAngsuran], which refers to at what month and year the [Pembayaran]
 * should be.
 *
 */
data class Pembayaran(
    val termin: String,
    val tanggal: String,
    val jumlahUangDibayar: String = "",
    var totalUangMasuk: String = "",
    var presentase: Double = 0.0,
    var sisaBelumTerbayar: String = "",
    val keterangan: String,
    val timeMillis: Long,
    var sudahIsiFotoPembayaran: Boolean = false,
    var sudahAmbilKuitansi: Boolean = false,
    val bulanAngsuran: BulanAngsuran = BulanAngsuran.defaultToTanggalPembayaran(tanggal),
) {
    val parsedJumlahUangDibayar = NumberUtil.formatStringToLong(jumlahUangDibayar)

    fun getUrutan(): Int {
        return termin.split(" ")[1].toInt()
    }

    fun getJenisTermin(): String {
        return termin.split(" ")[0]
    }

    fun hitungPersentase(hargaKavling: HargaKavling): Double {
        val floatTotalUangMasuk = NumberUtil.formatStringToLong(totalUangMasuk).toFloat()
//        val floatHargaKavling = hargaKavling.toFloat()
        // Fix persentase calculation not included Tambah Luasan
        val floatHargaKavling = hargaKavling.hargaLong.toFloat()
        val persentase = floatTotalUangMasuk.div(floatHargaKavling).let {
            val bigDecimal = it.toBigDecimal().setScale(4, RoundingMode.HALF_UP)
            return@let bigDecimal.multiply(BigDecimal.valueOf(100))
        }

        return persentase.toDouble()
    }

    fun hitungPersentase(hargaRumah: HargaRumahIndenBooking): Double {
        val totalUangMasuk = BigDecimal(NumberUtil.formatStringToLong(totalUangMasuk))
        val hargaRumahDanTambahLuasan = BigDecimal(hargaRumah.hargaDanTambahLuasan)

        val persentase = totalUangMasuk
            .divide(hargaRumahDanTambahLuasan, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))

        return persentase.toDouble()
    }

    fun isEqualTo(other: Pembayaran): Boolean {
        return (termin == other.termin)
                && (tanggal == other.tanggal)
                && (jumlahUangDibayar == other.jumlahUangDibayar)
                && (keterangan == other.keterangan)
                && (bulanAngsuran == other.bulanAngsuran)
    }

    fun validate(): Boolean {
        return parsedJumlahUangDibayar > 0
    }

    companion object {
        const val PEMBAYARAN_KAVLING = 0
        const val PEMBAYARAN_INDEN_BOOKING = 1

        const val FILTER_USING_TANGGAL = 0
        const val FILTER_USING_BULAN_ANGSURAN = 1

        /**
         * Iteratively sums up [Pembayaran.jumlahUangDibayar] in every element of
         * [listPembayaran], by converting [Pembayaran.jumlahUangDibayar] into [Long] type
         * using [NumberUtil.formatLongToString].
         *
         * If [listPembayaran] is an [emptyList], will return zero.
         *
         * @param listPembayaran any [Pembayaran]'s [List]
         */
        fun hitungTotalUangMasuk(listPembayaran: List<Pembayaran>): Long {
            var mTotal = 0L

            listPembayaran.forEach {
                mTotal += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            }

            return mTotal
        }

        fun hitungTotalAllKavling(mapListPembayaran: Map<String, List<Pembayaran>?>): Long {
            var mTotal = 0L

            mapListPembayaran.keys.forEach { kavling ->
                mapListPembayaran[kavling]?.forEach { pembayaran ->
                    mTotal += pembayaran.parsedJumlahUangDibayar
                }
            }

            return mTotal
        }

        fun hitungTotalSisaBelumBayarWithTambahLuasan(hargaKavling: HargaKavling, jumlahUangMasukKavling: Long): Long {
            return hargaKavling.hargaDanTambahLuasan - jumlahUangMasukKavling
        }

        fun getSisaBelumTerbayar(pembayarans: List<Pembayaran>): String {
            return pembayarans.last().sisaBelumTerbayar
        }

        /**
         * Sorting [Pembayaran]'s [List]
         *
         * @param listPembayaran any [Pembayaran]'s [List].
         * @param sorter you can implement your own [PembayaranSorter] or use built-in
         * [TerminPembayaranSorter].
         *
         * @see [sortByTermin]
         */
        fun sortPembayaran(
            listPembayaran: List<Pembayaran>,
            sorter: PembayaranSorter,
        ) = sorter.sort(listPembayaran)

        /**
         * _Tanggal pembelian_ is simply the first index of sorted [Pembayaran]'s List.
         *
         * @param sortedListPembayaran you must sort this [List] first, use [sortPembayaran]
         * or [sortByTermin].
         * @throws Exception when [sortedListPembayaran] is empty.
         * @see tanggalPembelian
         */
        fun getTanggalPembelian(sortedListPembayaran: List<Pembayaran>) =
            if (sortedListPembayaran.isEmpty())
                throw Exception("Get tanggal pembelian gagal -> argumen sortedListPembayaran dengan list masih kosong tidak boleh!")
            else
                sortedListPembayaran.first().tanggal

        fun List<Pembayaran>?.filterPeriode(
            periode: PeriodeRekap,
            start: Date?,
            end: Date?,
            filterMode: Int = FILTER_USING_TANGGAL,
        ): List<Pembayaran>? {
            return when(periode) {
                PeriodeRekap.SEMUA -> this
                PeriodeRekap.TAHUN_INI ->  {
                    this?.filter { pembayaran ->
                        val tahunPembayaran = when (filterMode) {
                            FILTER_USING_TANGGAL -> Calendar.getInstance().let {
                                it.time = pembayaran.tanggal.toDate()
                                it.get(Calendar.YEAR)
                            }
                            FILTER_USING_BULAN_ANGSURAN -> pembayaran.bulanAngsuran.tahun
                            else -> throw IllegalArgumentException("Filter pembayaran mode $filterMode tidak diketahui!")
                        }

                        tahunPembayaran == Calendar.getInstance().get(Calendar.YEAR)
                    }
                }
                PeriodeRekap.BULAN_INI -> {
                    val rangeTanggal = DateUtil.getMonthlyRangeDate()
                    val tanggalPertama = rangeTanggal[0]
                    val tanggalTerakhir = rangeTanggal[1]

                    this?.filter { pembayaran ->
                        val tanggalPembayaran = when (filterMode) {
                            FILTER_USING_TANGGAL -> pembayaran.tanggal.toDate()
                            FILTER_USING_BULAN_ANGSURAN -> pembayaran.bulanAngsuran.date
                            else -> throw IllegalArgumentException("Filter pembayaran mode $filterMode tidak diketahui!")
                        }

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
                        val tanggalPembayaran = when (filterMode) {
                            FILTER_USING_TANGGAL -> pembayaran.tanggal.toDate()
                            else -> throw IllegalArgumentException("Periode filter MINGGU_INI tidak boleh menggunakan mode filter selain FILTER_USING_TANGGAL !")
                        }

                        tanggalPembayaran.isWithinRange(startDate, endDate)
                    }

                }
                PeriodeRekap.CUSTOM -> {
                    val rangeTanggal = getCustomRangeDate(start!!, end!!)
                    val startDate = rangeTanggal[0]
                    val endDate = rangeTanggal[1]

                    this?.filter {
                        val tanggalPembayaran = when (filterMode) {
                            FILTER_USING_TANGGAL -> it.tanggal.toDate()
                            FILTER_USING_BULAN_ANGSURAN -> it.bulanAngsuran.date
                            else -> throw IllegalArgumentException("Filter pembayaran mode $filterMode tidak diketahui!")
                        }

                        tanggalPembayaran.isWithinRange(startDate, endDate)
                    }
                }
            }
        }

        fun uangMasukPadaBulanDanTahunIni(
            pembayarans: List<Pembayaran>,
            bulan: Int, // Not Calendar type of Bulan!
            tahun: Int,
            filterMode: Int,
        ): Long {
            val rangeTanggal = DateUtil.getMonthlyRangeDate(bulan - 1, tahun)
            val awalTanggal = rangeTanggal[0]
            val akhirTanggal = rangeTanggal[1]

            val pembayaranPadaBulanTsb = pembayarans.filterPeriode(
                PeriodeRekap.CUSTOM, awalTanggal, akhirTanggal, filterMode,
            )
            return if (!pembayaranPadaBulanTsb.isNullOrEmpty()) {
                hitungTotalUangMasuk(pembayaranPadaBulanTsb)
            } else {
                0L
            }
        }

        suspend fun maskPembayaran(
            listPembayaran: List<Pembayaran>,
            hargaRumah: HargaRumahIndenBooking,
            onCekFotoPembayaran: suspend (termin: String) -> Boolean,
            onCekSudahAmbilKuitansi: suspend (termin: String) -> Boolean
        ): List<Pembayaran> {
            val terminPembayaranSorter = TerminPembayaranSorter()
            val sortedListPembayaran = sortPembayaran(listPembayaran, terminPembayaranSorter)
            val newListPembayaran = ArrayList<Pembayaran>()
            var totalUangMasuk = 0L

            sortedListPembayaran.forEach {
                totalUangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
                it.totalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
                it.presentase = it.hitungPersentase(hargaRumah)
                it.sisaBelumTerbayar = NumberUtil.formatLongToString(
                    hargaRumah.hargaDanTambahLuasan - totalUangMasuk
                )
                it.sudahIsiFotoPembayaran = onCekFotoPembayaran(it.termin)
                it.sudahAmbilKuitansi = onCekSudahAmbilKuitansi(it.termin)

                newListPembayaran.add(it)
            }

            return newListPembayaran
        }

        suspend fun maskPembayaran(
            listPembayaran: List<Pembayaran>,
            hargaKavling: HargaKavling,
            onCekFotoPembayaran: suspend (termin: String) -> Boolean,
            onCekSudahAmbilKuitansi: suspend (kavling: String, termin: String) -> Boolean,
        ): List<Pembayaran> {
            val terminPembayaranSorter = TerminPembayaranSorter()
            val sortedListPembayaran = sortPembayaran(listPembayaran, terminPembayaranSorter)
            val newListPembayaran = ArrayList<Pembayaran>()
            var totalUangMasuk = 0L

            sortedListPembayaran.forEach {
                totalUangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
                it.totalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
                it.presentase = it.hitungPersentase(hargaKavling)
                it.sisaBelumTerbayar = NumberUtil.formatLongToString(hargaKavling - totalUangMasuk)
                it.sudahIsiFotoPembayaran = onCekFotoPembayaran(it.termin)
                it.sudahAmbilKuitansi = onCekSudahAmbilKuitansi(hargaKavling.kavlingKode, it.termin)

                newListPembayaran.add(it)
            }

            return newListPembayaran
        }

        fun nextPembayaranSequence(
            pembayarans: List<Pembayaran>?,
            jenisTermin: JenisTermin
        ): String {
            // Check if not null listPembayaran.
            // If null returns "1"
            if (!pembayarans.isNullOrEmpty()) {
                // Check if any requested jenis pembayaran Exists
                val requestedJenisPembayaranList = pembayarans.filter {
                    it.termin.startsWith(jenisTermin.text)
                }
                return if (requestedJenisPembayaranList.isNotEmpty()) {
                    // If exists, then get the last index of the requested pembayaran.
                    // I speculate that the UseCase already do the sorting, so
                    // the last of Any Pembayaran Sequence should be on the last index.
                    val lastPembayaran = requestedJenisPembayaranList.last()

                    // +1 on the last number of urutan
                    val urutan = lastPembayaran.getUrutan()
                    urutan.plus(1).toString()
                } else {
                    "1"
                }
            } else {
                return "1"
            }
        }

        fun List<Pembayaran>.groupByTermins(jenisTermins: List<String>): Map<String, List<Pembayaran>> {
            fun getListByTermin(pembayaranList: List<Pembayaran>, jenisTermin: String): List<Pembayaran> {
                return pembayaranList.filter {
                    it.getJenisTermin() == jenisTermin
                }
            }

            return buildMap {
                jenisTermins.forEach { termin ->
                    put(termin, getListByTermin(this@groupByTermins, termin))
                }
            }
        }

        /**
         * Extension for [sortPembayaran].
         */
        fun List<Pembayaran>.sortByTermin()
                = sortPembayaran(this, TerminPembayaranSorter())

        /**
         * Extension for [getTanggalPembelian].
         *
         * Note: [Pembayaran]'s List must be sorted first, you can use
         * either [sortPembayaran] or [sortByTermin] to do that.
         */
        fun List<Pembayaran>.tanggalPembelian()
            = getTanggalPembelian(this)

        /**
         * Extension for [hitungTotalUangMasuk].
         */
        fun List<Pembayaran>.totalUangMasuk()
            = hitungTotalUangMasuk(this)
    }

    enum class JenisTermin(val text: String) {
        ITJ("ITJ"),
        DP("DP"),
        TERMIN("Termin"),
    }

    interface PembayaranSorter {
        fun sort(pembayaranList: List<Pembayaran>): List<Pembayaran>
    }
}

/**
 * This class contains a full-fledged made-by-me algorithm to sort pembayaran according to ITJ, DP, and Termin order.
 *
 * Briefly, a "Pembayaran" object have a "Jenis" and an "Urutan" components. For example,
 * a "Pembayaran DP 5" has a "jenis" of "DP" and "5" of urutan components. In this algorithm, we need to sort
 * both of these components via two differents subroutines: Grouping the "Jenis" and Ordering the "Urutan".
 *
 * Given set of pembayaran, represented a List<Pembayaran>, we need to group them into "Jenis Pembayaran".
 * The result are three groups of List<Pembayaran> -> ITJ Group, DP Group, and Termin Group.
 *
 * After we group the pembayaran, we need to further sort each group according to their numerical order.
 */
class TerminPembayaranSorter: Pembayaran.PembayaranSorter {

    override fun sort(pembayaranList: List<Pembayaran>): List<Pembayaran> {
        return groupPembayaranOnTerminAndSort(pembayaranList)
    }

    private fun groupPembayaranOnTerminAndSort(listPembayaran: List<Pembayaran>): List<Pembayaran> {
        val itjGroup = createGroupPembayaran(Pembayaran.JenisTermin.ITJ.text, listPembayaran)
        val dpGroup = createGroupPembayaran(Pembayaran.JenisTermin.DP.text, listPembayaran)
        val terminGroup = createGroupPembayaran(Pembayaran.JenisTermin.TERMIN.text, listPembayaran)

        val sortedPembayaran = mutableListOf<Pembayaran>()
        with(sortedPembayaran) {
            // This order of execution of addAll() is important!
            addAll(itjGroup.listPembayaran)
            addAll(dpGroup.listPembayaran)
            addAll(terminGroup.listPembayaran)
        }


        return sortedPembayaran
    }

    private fun createGroupPembayaran(jenisPembayaran: String, listPembayaran: List<Pembayaran>): GroupPembayaran {
        val filteredListPembayaran = listPembayaran.filter { pembayaran ->
            val termin = pisahkanTerminDanUrutan(pembayaran.termin)

            termin[Komponen.JENIS]!! == jenisPembayaran
        }

        return GroupPembayaran(jenisPembayaran, filteredListPembayaran)
    }

    private companion object {
        fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf<String, String>(
                Pair(Komponen.JENIS, terminDanUrutan[0]),
                Pair(Komponen.URUTAN, terminDanUrutan[1]),
            )
        }
    }

    private object Komponen {
        const val JENIS = "jenis"
        const val URUTAN = "urutan"
    }

    private data class GroupPembayaran(
        val jenisPembayaran: String,
        var listPembayaran: List<Pembayaran>
    ) {

        init {
            this.listPembayaran = listPembayaran.sortedBy {
                val termin = pisahkanTerminDanUrutan(it.termin)

                termin[Komponen.URUTAN]!!.toInt()
            }
        }

    }
}