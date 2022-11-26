package net.bagusekasaputra.griyakampoengtkw.domain

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
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

    @Test
    fun test_simsalabim() {
        val randomListPembayaran = PembayaranUtil.generatePembayaran()


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
        fun generateRandomTanggal(): String {
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

            val randomIndex = Random.nextInt(listDate.indices)

            return formatDate(listDate[randomIndex])
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            return formatter.format(date)
        }
    }
}