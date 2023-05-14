package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import java.util.Calendar

data class PembayaranBulanan(
    val kavling: String,
    val bulan: Int, // NOT  Calendar type of Bulan
    val tahun: Int,
    val listPembayaran: List<Pembayaran>,
    val baselinePembayaran: BaselinePembayaran,
    var kelunasan: Kelunasan = Kelunasan.NIL,
    var alokasi: Long = 0L,
) {
    val uangMasuk = Pembayaran.hitungTotalUangMasuk(listPembayaran)
    val tunggakan: Long
        get() = baselinePembayaran.jumlahUang - uangMasuk

    val bulanStr = DateUtil.namaBulanShort(bulan)
    val parsedBulanTahun = "$bulanStr $tahun"
    val bulanTahunDate = "1/$bulan/$tahun".toDate()

    enum class Kelunasan(val str: String) {
        LUNAS("LUNAS"), KURANG("KURANG"), NIL("NIL")
    }


    companion object {
        fun groupPembayaranIntoBulanan(kavling: String, baselinePembayaran: BaselinePembayaran, sortedListPembayaran: List<Pembayaran>): List<PembayaranBulanan> {
            val listPembayaranBulanan = mutableListOf<PembayaranBulanan>()

            var listBulanTahun = mutableListOf<Pair<Int, Int>>()
            sortedListPembayaran.forEach { pembayaran ->
                val tanggalPembayaran = Calendar.getInstance().apply {
                    time = pembayaran.tanggal.toDate()
                }
                val bulanPembayaran = tanggalPembayaran.get(Calendar.MONTH)
                val tahunPembayaran = tanggalPembayaran.get(Calendar.YEAR)

                listBulanTahun.add(Pair(bulanPembayaran, tahunPembayaran))
            }
            listBulanTahun = listBulanTahun.distinct().toMutableList() // Filter out duplicate

            listBulanTahun.forEach { bulanTahun ->
                val rangeTanggal = DateUtil.getMonthlyRangeDate(bulanTahun.first, bulanTahun.second)
                val tanggalPertama = rangeTanggal[0]
                val tanggalTerakhir = rangeTanggal[1]
                val listOnlySpecifiedBulan = sortedListPembayaran.filterPeriode(PeriodeRekap.CUSTOM, tanggalPertama, tanggalTerakhir)

                if (listOnlySpecifiedBulan != null) {
                    listPembayaranBulanan.add(PembayaranBulanan(kavling,
                        bulanTahun.first.plus(1), // Bulan yang ada diisini pake formatnya Calendar, so harus +1
                        bulanTahun.second,
                        listOnlySpecifiedBulan,
                        baselinePembayaran,
                    ))
                }
            }

            return PembayaranBulanan.sort(listPembayaranBulanan)
        }

        fun sort(listPembayaranBulanan: List<PembayaranBulanan>): List<PembayaranBulanan> {
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

        fun mask(pembayaranBulanans: List<PembayaranBulanan>): List<PembayaranBulanan> {
            val newList = mutableListOf<PembayaranBulanan>()
            var alokasi = 0L
            pembayaranBulanans.forEach {
                alokasi += -1 * it.tunggakan
                it.alokasi = alokasi

                it.kelunasan = if (it.tunggakan <= 0) Kelunasan.LUNAS
                    else Kelunasan.KURANG

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
                    kelunasan = Kelunasan.NIL,
                    alokasi = lastAlokasi - baselinePembayaran.jumlahUang,
                ))
            }

            return newList
        }
    }
}