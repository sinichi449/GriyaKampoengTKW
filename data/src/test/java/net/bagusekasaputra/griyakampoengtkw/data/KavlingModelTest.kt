package net.bagusekasaputra.griyakampoengtkw.data

import com.google.gson.Gson
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.CombinedKavling
import org.junit.Assert
import org.junit.Test

class KavlingModelTest {

    private val remoteKavlingData = """
        {
            "active": false,
            "kode": "A19 + A20",
            "type": "Type 36",
            "ukuran": "12x24",
            "warna": "#E93303",
            "isCombined": true
        }
    """.trimIndent()

    private val model = Gson().fromJson(remoteKavlingData, KavlingModel::class.java)

    @Test
    fun getKavlingKodeTest() {
        Assert.assertEquals(KavlingModel(
            kode = "A19 + A20",
            warna = "#E93303",
            active = false,
            ukuran = "12x24",
            type = "Type 36",
            isCombined = true
        ), model)
    }

    @Test
    fun getListKodeTest() {
        val kodeList = listOf("A19", "A20")

        Assert.assertEquals(kodeList, model.getListKode())
    }

    @Test
    fun getNumKodeTest() {
        Assert.assertEquals(19, model.getNumkode())
    }

    @Test
    fun getBlockKodeTest() {
        Assert.assertEquals("A", KavlingModel.getBlockKode(model.kode))
    }

    @Test
    fun mapToDomainModelTest() {
        val result = MyObjectMapper.mapKavling(model)
        val expected = CombinedKavling(
            kavlingKodeList = listOf("A19", "A20"),
            belumIsi = false,
            warna = "#E93303",
            ukuran = "12x24",
            type = "Type 36",
            numKode = 19,
        )

        Assert.assertEquals(expected, result)
    }
}