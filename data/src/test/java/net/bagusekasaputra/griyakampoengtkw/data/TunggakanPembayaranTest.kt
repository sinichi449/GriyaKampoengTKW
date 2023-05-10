package net.bagusekasaputra.griyakampoengtkw.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.PeriodeRekap
import org.junit.Test
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.Calendar

class TunggakanPembayaranTest {

    @Test
    fun pembayaran_bulanan_test() {
        val listPembayaranBulanan = Pembayaran.groupIntoBulanan("A11", getListPembayaran())
        val jsonResult = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(listPembayaranBulanan)

        val fileWriter = FileWriter("tes_pembayaran_bulanan.json")
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(jsonResult)
        bufferedWriter.close()
    }

    @Test
    fun apakah_pembayaran_bulanan_sudah_urut() {
        val sortedListPembayaran = Pembayaran.sortPembayaran(getListPembayaran())
        val listPembayaranBulanan = Pembayaran.groupIntoBulanan("A11", sortedListPembayaran)
        val jsonResult = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(listPembayaranBulanan)

        val fileWriter = FileWriter("tes_sorted_pembayaran_bulanan.json")
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(jsonResult)
        bufferedWriter.close()
    }

    @Test
    fun buat_list_tunggakan() {
        val kavling = "A11"
        val sortedListPembayaran = Pembayaran.sortPembayaran(getListPembayaran())
        val listPembayaranBulanan = Pembayaran.groupIntoBulanan(kavling, sortedListPembayaran)
        val baseline = BaselinePembayaran(kavling, 48, 4_687_500L, 15)

        val listTunggakan = mutableListOf<Pair<PembayaranBulanan, String>>()
        listPembayaranBulanan.forEach {
            val jumlahTunggakanStr = NumberUtil.formatLongToString(it.jumlahUangTunggakan(baseline))
            listTunggakan.add(Pair(it, jumlahTunggakanStr))
        }

        val jsonResult = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(listTunggakan)

        val fileWriter = FileWriter("tes_tunggakan_pembayaran.json")
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(jsonResult)
        bufferedWriter.close()

        var totalTunggakan = 0L
        listTunggakan.forEach {
            val tunggakan = NumberUtil.formatStringToLong(it.second)
            totalTunggakan += tunggakan
        }
        println("Total Tunggakan : ${NumberUtil.formatLongToString(totalTunggakan)}")
    }

    private fun getListPembayaran(): List<Pembayaran> {
        val json = "{\"DP 1\":{\"fullTermin\":\"DP 1\",\"jumlahUangDibayar\":1000000,\"keterangan\":\"Dp1\",\"tanggal\":\"19/12/2022\",\"termin\":\"DP\",\"timeMillis\":1671422391736,\"urutan\":1},\"DP 10\":{\"fullTermin\":\"DP 10\",\"jumlahUangDibayar\":2000000,\"keterangan\":\"DP 10\",\"tanggal\":\"15/02/2023\",\"termin\":\"DP\",\"timeMillis\":1676439797080,\"urutan\":10},\"DP 11\":{\"fullTermin\":\"DP 11\",\"jumlahUangDibayar\":800000,\"keterangan\":\"Dp 11\",\"tanggal\":\"18/02/2023\",\"termin\":\"DP\",\"timeMillis\":1676684542066,\"urutan\":11},\"DP 12\":{\"fullTermin\":\"DP 12\",\"jumlahUangDibayar\":500000,\"keterangan\":\"Dp 12\",\"tanggal\":\"06/03/2023\",\"termin\":\"DP\",\"timeMillis\":1678084942499,\"urutan\":12},\"DP 13\":{\"fullTermin\":\"DP 13\",\"jumlahUangDibayar\":3000000,\"keterangan\":\"Dp13 \",\"tanggal\":\"13/03/2023\",\"termin\":\"DP\",\"timeMillis\":1678683068767,\"urutan\":13},\"DP 14\":{\"fullTermin\":\"DP 14\",\"jumlahUangDibayar\":1000000,\"keterangan\":\"DP 14\",\"tanggal\":\"22/03/2023\",\"termin\":\"DP\",\"timeMillis\":1679451012550,\"urutan\":14},\"DP 15\":{\"fullTermin\":\"DP 15\",\"jumlahUangDibayar\":3100000,\"keterangan\":\"Dp 15 + pemindahan kav B6 - A11\",\"tanggal\":\"30/04/2023\",\"termin\":\"DP\",\"timeMillis\":1682865453122,\"urutan\":15},\"DP 16\":{\"fullTermin\":\"DP 16\",\"jumlahUangDibayar\":3000000,\"keterangan\":\"Dp 16\",\"tanggal\":\"03/05/2023\",\"termin\":\"DP\",\"timeMillis\":1683124294180,\"urutan\":16},\"DP 2\":{\"fullTermin\":\"DP 2\",\"jumlahUangDibayar\":1375000,\"keterangan\":\"-\",\"tanggal\":\"19/12/2022\",\"termin\":\"DP\",\"timeMillis\":1671459285668,\"urutan\":2},\"DP 3\":{\"fullTermin\":\"DP 3\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"uang masuk TF dari MB Novi 6.650.0000 uang MB Lela 5 jt MB Hesti 1 JT sisa 650.000 pembayaran hutang\",\"tanggal\":\"21/12/2022\",\"termin\":\"DP\",\"timeMillis\":1671590797816,\"urutan\":3},\"DP 4\":{\"fullTermin\":\"DP 4\",\"jumlahUangDibayar\":13000000,\"keterangan\":\"Dp4\",\"tanggal\":\"25/12/2022\",\"termin\":\"DP\",\"timeMillis\":1671973635206,\"urutan\":4},\"DP 5\":{\"fullTermin\":\"DP 5\",\"jumlahUangDibayar\":4000000,\"keterangan\":\"Dp 5 uang masuk jam 19.53\",\"tanggal\":\"01/01/2023\",\"termin\":\"DP\",\"timeMillis\":1672619632335,\"urutan\":5},\"DP 6\":{\"fullTermin\":\"DP 6\",\"jumlahUangDibayar\":7130000,\"keterangan\":\"dp 6 masuk tgl 2 jam 00.30\",\"tanggal\":\"02/01/2023\",\"termin\":\"DP\",\"timeMillis\":1672619724075,\"urutan\":6},\"DP 7\":{\"fullTermin\":\"DP 7\",\"jumlahUangDibayar\":500000,\"keterangan\":\"DP 7\",\"tanggal\":\"02/01/2023\",\"termin\":\"DP\",\"timeMillis\":1672634612482,\"urutan\":7},\"DP 8\":{\"fullTermin\":\"DP 8\",\"jumlahUangDibayar\":1000000,\"keterangan\":\"Dp8\",\"tanggal\":\"16/01/2023\",\"termin\":\"DP\",\"timeMillis\":1673855741812,\"urutan\":8},\"DP 9\":{\"fullTermin\":\"DP 9\",\"jumlahUangDibayar\":4305000,\"keterangan\":\"Dp9\",\"tanggal\":\"30/01/2023\",\"termin\":\"DP\",\"timeMillis\":1675046478743,\"urutan\":9},\"ITJ 1\":{\"fullTermin\":\"ITJ 1\",\"jumlahUangDibayar\":5000000,\"keterangan\":\"ITJ \",\"tanggal\":\"09/12/2022\",\"termin\":\"ITJ\",\"timeMillis\":1670575902439,\"urutan\":1}}"
        val type = object : TypeToken<HashMap<String, Pembayaran>>() {}

        val mapPembayaran = Gson().fromJson<HashMap<String, Pembayaran>>(json, type.type)
        val listPembayaran = mutableListOf<Pembayaran>()
        mapPembayaran.keys.forEach { termin ->
            val pembayaran = mapPembayaran[termin]
            pembayaran?.also {
                listPembayaran.add(Pembayaran(
                    termin = termin,
                    tanggal = it.tanggal,
                    jumlahUangDibayar = it.jumlahUangDibayar,
                    totalUangMasuk = "0",
                    presentase = 0.0,
                    sisaBelumTerbayar = "0",
                    keterangan = it.keterangan,
                    timeMillis = it.timeMillis,
                    sudahIsiFotoPembayaran = false,
                ))
            }
        }

        return listPembayaran
    }
}