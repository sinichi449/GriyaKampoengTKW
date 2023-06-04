package net.bagusekasaputra.griyakampoengtkw.domain.entity

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class KavlingTest {

    @Test
    fun sortingSingleBlockKavlings_shouldCorrect() {
        val blockKode = "Z"
        val sortedKavlingList = mutableListOf<Kavling>().apply {
            val randomKavlingSize = Random.nextInt(from = 10, until = 100)
            repeat(randomKavlingSize) { numKode ->
                add(Kavling(kode = "${blockKode}${numKode + 1}", warna = "", ukuran = "", type = ""))
            }
        }

        val shuffledKavlingList = sortedKavlingList.shuffled()

        Kavling.sortKavling(shuffledKavlingList, SingleBlockKavlingSorter()).forEachIndexed { index, kavling ->
            Assert.assertEquals(sortedKavlingList[index].kode, kavling.kode)
        }
    }


}