package net.bagusekasaputra.griyakampoengtkw.data.backup

import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    data class Pembayaran(
        val termin: String = "",
        val urutan: Int = 0,
        val tanggal: String = "",
        val jumlahUangDibayar: Long = 0L,
        val keterangan: String = "",
        val timeMillis: Long = 0L,
    )

    @Test
    fun test_parsing_hashmap_to_json() {
        val dataPembayaran = HashMap<String, Pembayaran>()

        val timeMillis = System.currentTimeMillis()
        dataPembayaran["ITJ 1"] = Pembayaran(
            termin = "ITJ",
            urutan = 1,
            tanggal = "16/04/2023",
            jumlahUangDibayar = 1_500_000L,
            keterangan = "",
            timeMillis = timeMillis,
        )
        dataPembayaran["DP 1"] = Pembayaran(
            termin = "DP",
            urutan = 1,
            tanggal = "16/04/2023",
            jumlahUangDibayar = 3_500_000L,
            keterangan = "",
            timeMillis = timeMillis,
        )
        dataPembayaran["DP 2"] = Pembayaran(
            termin = "DP",
            urutan = 2,
            tanggal = "16/04/2023",
            jumlahUangDibayar = 7_500_000L,
            keterangan = "",
            timeMillis = timeMillis,
        )

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(dataPembayaran)

        println(json)
    }
}