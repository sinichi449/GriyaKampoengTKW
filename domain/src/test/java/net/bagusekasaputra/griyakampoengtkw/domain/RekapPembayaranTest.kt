package net.bagusekasaputra.griyakampoengtkw.domain

import org.junit.Test
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

class RekapPembayaranTest {

    private data class PembayaranAlt(
        val tanggal: String,
        val jumlahUangDibayar: Long,
    )

    private data class FeeMarketingAlt(
        val tanggal: String,
        val biaya: Long,
    )

    private data class BiayaMarketingAlt(
        val tanggal: String,
        val harga: Long,
    )

    private data class RekapKavling(
        val kavling: String,
        val listPembayaran: List<PembayaranAlt>?,
        val feeMarketing: FeeMarketingAlt?,
        val listBiayaMarketing: List<BiayaMarketingAlt>?,
    ) {
        fun getTotalCuan(): Long {
            val totalPembayaran = getTotalPembayaran()
            val uangFeeMarketer = feeMarketing?.biaya ?: 0L
            val totalBiayaMarketing = getTotalBiayaMarketing()

            return totalPembayaran - uangFeeMarketer - totalBiayaMarketing
        }

        private fun getTotalPembayaran(): Long {
            var totalPembayaran = 0L

            listPembayaran?.forEach {
                totalPembayaran += it.jumlahUangDibayar
            }

            return totalPembayaran
        }

        private fun getTotalBiayaMarketing(): Long {
            var totalBiayaMarketing = 0L

            listBiayaMarketing?.forEach {
                totalBiayaMarketing += it.harga
            }

            return totalBiayaMarketing
        }
    }


    private fun printHeader() {
        repeat(50) { print("=") }
        println("")
    }

    private fun Double.juta(): Long {
        val bigDecimal = BigDecimal(this)
        val juta = BigDecimal(10.0.pow(6.0))
        return bigDecimal.multiply(juta).toLong()
    }

    @Test
    fun test_simsalabim() {
        val tanggal = "23/09/2022"
        val listPembayaranC9 = listOf(
            PembayaranAlt(tanggal, 1.0.juta()),
            PembayaranAlt(tanggal, 2.0.juta()),
            PembayaranAlt(tanggal, 2.0.juta()),
        )
        val listBiayaMarketingC9 = listOf(
            BiayaMarketingAlt(tanggal, 0.45.juta())
        )
        val rekapKavling = RekapKavling(
            kavling = "C9",
            listPembayaran = listPembayaranC9,
            feeMarketing = FeeMarketingAlt(tanggal, 4.4.juta()),
            listBiayaMarketing = listBiayaMarketingC9,
        )
    }

    private object RekapKavlingUtil {
        fun generateRandomRekapAllKavling(): List<RekapKavling> {
            val listKavling = KavlingUtil.getAllKavlings()
            val listRekapKavling = mutableListOf<RekapKavling>()
            listKavling.forEach { kavling ->
                listRekapKavling.add(
                    RekapKavling(
                        kavling = kavling,
                        listPembayaran = PembayaranUtil.generatePembayaran(),
                        feeMarketing = MarketingUtil.generateFeeMarketing(),
                        listBiayaMarketing = MarketingUtil.generateListBiayaMarketing(),
                    )
                )
            }

            return listRekapKavling
        }
    }

    private object KavlingUtil {
        fun getAllKavlings(): List<String> {
            val blocksWithSum = mapOf(
                Pair("A", 14),
                Pair("B", 20),
                Pair("C", 9),
            )

            val listKavlings = mutableListOf<String>()
            blocksWithSum.keys.forEach { block ->
                val totalUnit = blocksWithSum[block] ?: 0

                (1..totalUnit).forEach { noKavling ->
                    listKavlings.add("$block$noKavling")
                }
            }

            return listKavlings
        }
    }

    private object PembayaranUtil {
        fun generatePembayaran(): List<PembayaranAlt> {
            val numPembayaran = Random.nextInt(5..50)

            val listPembayaran = mutableListOf<PembayaranAlt>()
            repeat(numPembayaran) {
                listPembayaran.add(
                    PembayaranAlt(
                        tanggal = DateUtil.generateRandomTanggal(),
                        jumlahUangDibayar = randomJumlahUangMasuk(),
                    )
                )
            }

            return listPembayaran
        }

//        fun getDiscretePembayaran(): List<PembayaranAlt> {
//            return listOf(
//                PembayaranAlt(
//                    tanggal = ""
//                )
//            )
//        }

        private fun randomJumlahUangMasuk(): Long {
            return Random.nextLong(10L, 150L) * 100000L
        }
    }

    private object MarketingUtil {
        fun generateFeeMarketing(): FeeMarketingAlt {
            return FeeMarketingAlt(
                tanggal = DateUtil.generateRandomTanggal(),
                biaya = generateRandomBiaya(),
            )
        }

        fun generateListBiayaMarketing(): List<BiayaMarketingAlt> {
            val jumlahBiayaMarketing = Random.nextInt(1..8)

            val listBiayaMarketing = mutableListOf<BiayaMarketingAlt>()
            (1..jumlahBiayaMarketing).forEach {
                listBiayaMarketing.add(
                    BiayaMarketingAlt(
                        tanggal = DateUtil.generateRandomTanggal(),
                        harga = generateRandomKecil(),
                    )
                )
            }

            return listBiayaMarketing
        }

        private fun generateRandomBiaya(): Long {
            return Random.nextLong(10L..60L) * 100000L
        }

        private fun generateRandomKecil(): Long {
            return Random.nextLong(100L..500L) * 100L
        }
    }

    private object DateUtil {
        enum class Periode {
            MINGGU_INI, BULAN_INI, TAHUN_INI
        }
        private fun Calendar.resetHours(): Calendar {
            this.set(Calendar.HOUR_OF_DAY, 0)
            this.set(Calendar.MINUTE, 0)
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)

            return this
        }

        fun filterPeriode(periode: Periode, listDate: List<Date>): List<Date> {
            val listRangePeriode = generateRangeDate(periode)

            return listRangePeriode
                .map {
                    // Set the hours to 00:00:00
                    Calendar.getInstance()
                        .apply { time = it }
                        .resetHours()
                        .time
                }
                .filter {
                    listDate.contains(it)
                }
        }

        fun generateRangeDate(periode: Periode): List<Date> {
            val hariIni = Calendar.getInstance().resetHours()

            val specifiedPeriode = Calendar.getInstance().apply {
                when (periode) {
                    Periode.MINGGU_INI -> add(Calendar.DAY_OF_MONTH, -7)
                    Periode.BULAN_INI -> set(Calendar.DAY_OF_MONTH, 1) // set to first date of specific month
                    Periode.TAHUN_INI -> {
                        // set to January 1st
                        set(Calendar.MONTH, Calendar.JANUARY)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            }
                .resetHours()

            val listTanggal = mutableListOf<Date>()
            // initial
            listTanggal.add(specifiedPeriode.time)
            while (specifiedPeriode.compareTo(hariIni) < 0) {
                val temp = Calendar.getInstance().apply {
                    specifiedPeriode.add(Calendar.DAY_OF_MONTH, 1)

                    time = specifiedPeriode.time
                }
                listTanggal.add(temp.time)
            }

            return listTanggal
        }

        fun generateRandomBulanIni(amount: Int): List<Date> {
            val startDate = Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_MONTH, 1) } // start tanggal 1
                .resetHours()
            val endDate = Calendar.getInstance() // end hari ini
                .resetHours()

            val listDate = mutableListOf<Date>().apply {
                repeat(amount) {
                    while (startDate.compareTo(endDate) < 0) {
                        val newDate = Calendar.getInstance().apply {
                            startDate.add(Calendar.DAY_OF_MONTH, 1)
                            time = startDate.time
                        }

                        add(newDate.time)
                    }
                }
            }

            val randomIndexes = mutableListOf<Int>().apply {
                repeat(amount) {
                    add(Random.nextInt(listDate.indices))
                }
            }
            val listRandomDate = mutableListOf<Date>().apply {
                randomIndexes.forEach {
                    add(listDate[it])
                }
            }

            return listRandomDate
        }

        fun generateStart2022(amount: Int=-1): List<Date> {
            val startDate = Calendar.getInstance().apply { set(2022, Calendar.JANUARY, 1) }
            val endDate = Calendar.getInstance() // end date is today

            val listTanggal = mutableListOf<Date>()
            while (startDate.compareTo(endDate) < 1) {
                val newCalendar = Calendar.getInstance().apply {
                    startDate.add(Calendar.DAY_OF_MONTH, 1)

                    time = startDate.time
                }
                listTanggal.add(newCalendar.time)
            }

            return if (amount == -1) {
                listTanggal
            } else if (amount >= listTanggal.size) {
                throw ArrayIndexOutOfBoundsException("The amount is too high, the maximum is ${listTanggal.size-1}")
            } else {
                val newListTanggal = mutableListOf<Date>()
                (1..amount).forEach {
                    val randomIndex = Random.nextInt(listTanggal.indices)
                    newListTanggal.add(listTanggal[randomIndex])
                }

                newListTanggal
            }
        }

        fun generateListOrderedTanggal(): List<String> {
            val startDate = Calendar.getInstance() // today
            val endDate = Calendar.getInstance().apply { set(2026, Calendar.MAY, 24) }

            val listDate = mutableListOf<Date>()
            while (startDate.compareTo(endDate) < 1) {
                val newCalendar = Calendar.getInstance().apply {
                    startDate.add(Calendar.DAY_OF_MONTH, 1)

                    this.time = startDate.time
                }
                listDate.add(newCalendar.time)
            }

            val listTanggalStr = mutableListOf<String>()
            listDate.forEach {
                listTanggalStr.add(formatDate(it))
            }

            return listTanggalStr
        }

        fun generateRandomTanggal(): String {
            val listDate = generateListOrderedTanggal()

            val randomIndex = Random.nextInt(listDate.indices)

            return listDate[randomIndex]
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            return formatter.format(date)
        }
    }

    fun String.toDate(): Date {
        val tanggal = this.split("/")[0].toInt()
        val bulan = this.split("/")[1].toInt() - 1
        val tahun = this.split("/")[2].toInt()

        val calendar = Calendar.getInstance().apply { set(tahun, bulan, tanggal) }

        return calendar.time
    }

    fun Date.toSlashedString(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        return formatter.format(this)
    }
}