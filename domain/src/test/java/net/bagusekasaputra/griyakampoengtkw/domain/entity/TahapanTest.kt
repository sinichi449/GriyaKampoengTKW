package net.bagusekasaputra.griyakampoengtkw.domain.entity

import org.junit.Assert
import org.junit.Test

class TahapanTest {

    @Test
    fun parsedNama_shouldCorrect() {
        val tahapanList = mutableListOf<Tahapan>()
        val correctParsedTahapan = mutableListOf<String>()

        repeat(50) {
            tahapanList.add(Tahapan("TAHAP_$it"))
            correctParsedTahapan.add("Tahap $it")
        }

        tahapanList.forEachIndexed { index, tahapan ->
            Assert.assertEquals(correctParsedTahapan[index], tahapan.nama)
        }
    }

}