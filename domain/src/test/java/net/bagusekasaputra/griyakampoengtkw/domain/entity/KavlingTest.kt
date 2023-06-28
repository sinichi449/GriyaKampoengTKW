package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.CombinedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.StandardKavling
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class KavlingTest {

    /**
     * Functional Test
     */

    private val combinedKavling = CombinedKavling(
        kavlingKodeList = listOf("A19", "A20"),
        belumIsi = false,
        warna = "#E93303",
        ukuran = "12X24",
        type = "Type 36",
        numKode = 19,
    )

    @Test
    fun blockKodeTest() {
        Assert.assertEquals("A", combinedKavling.blockKode)
    }

    @Test
    fun kavlingKodeTest() {
        Assert.assertEquals("A19 + A20", combinedKavling.kode)
    }

    @Test
    fun sortingSingleBlockKavlings_shouldCorrect() {
        val blockKode = "Z"
        val sortedKavlingList = mutableListOf<Kavling>().apply {
            val randomKavlingSize = Random.nextInt(from = 10, until = 100)
            repeat(randomKavlingSize) { numKode ->
                add(StandardKavling(kode = "${blockKode}${numKode + 1}", warna = "", ukuran = "", type = ""))
            }
        }

        val shuffledKavlingList = sortedKavlingList.shuffled()

        Kavling.sortKavling(shuffledKavlingList, SingleBlockKavlingSorter()).forEachIndexed { index, kavling ->
            Assert.assertEquals(sortedKavlingList[index].kode, kavling.kode)
        }
    }


    @Test
    fun exclusionForRekap_shouldCorrect() {
        val blockKode = "A"
        val kavlingSize = 100
        val sortedKavlingList = mutableListOf<String>().apply {
            repeat(kavlingSize) { numKode ->
                add("${blockKode}${numKode + 1}")
            }
        }

        val exclusionList = mutableListOf<String>().apply {
            val randomExclusionSize = Random.nextInt(from = 1, until = kavlingSize)
            repeat(randomExclusionSize) {
                add("${blockKode}${it + 1}")
            }
        }
        val filteredKavling = Kavling.excludeKavlingKode(sortedKavlingList, exclusionList)

        Assert.assertEquals(false, filteredKavling.containsAll(exclusionList))
    }

}