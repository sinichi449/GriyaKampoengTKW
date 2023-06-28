package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.KavlingAndProgress.Companion.sortByKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.ProgressKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.StandardKavling
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class KavlingAndProgressTest {

    /**
     * Functional Tests
     */
    @Test
    fun persentaseBulanIni_shouldBasedOnTotalTunggakan() {

    }

    /**
     * Conditional Tests
     */
    @Test
    fun givenUnsortedListOfThis_shouldSortedByKavling() {
        val blok = "C"
        val sizeKavling = randomKavlingSize()
        val sortedItems = buildList {
            repeat(sizeKavling) {
                val numKavling = it + 1
                val kode = "${blok}${numKavling}"
                val kavling = StandardKavling.EMPTY(kode)
                val progressKavling = ProgressKavling.EMPTY(kode)

                add(KavlingAndProgress(
                    blok = blok,
                    kavling = kavling,
                    progress = progressKavling,
                ))
            }
        }
        val shuffledItems = sortedItems.shuffled()

        Assert.assertEquals(sortedItems, shuffledItems.sortByKavling(blok))
    }

    @Test
    fun givenMultipleBlokListProvided_whenSortingListOfThis_shouldThrowUnsupportedOperationException() {
        val bloks = listOf("A", "B")
        val kavlingSize =  randomKavlingSize()
        val multipleBlokList = buildList {
            repeat(kavlingSize) { index ->
                // take the first `bloks`'s element when `index` is an even number
                // or take the second element when it is an odd number.
                val blok = if (index % 2 == 0)
                    bloks[0] else bloks[1]

                val numKavling = index + 1
                val kode = "${blok}${numKavling}"

                add(KavlingAndProgress(
                    blok = blok,
                    kavling = StandardKavling.EMPTY(kode),
                    progress = ProgressKavling.EMPTY(kode),
                ))
            }
        }

        Assert.assertThrows(UnsupportedOperationException::class.java) {
            multipleBlokList.sortByKavling(bloks.first())
        }
    }

    private fun randomKavlingSize() = Random.nextInt(from = 5, until = 20)
}