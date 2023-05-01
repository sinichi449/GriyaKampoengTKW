package net.bagusekasaputra.griyakampoengtkw.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

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

    @Test
    fun test_menghilangkan_spasi() {
        val inputText = "biaya pengiriman surat "

        val lastIndex = inputText.length - 1

        var newInput = inputText
        if (inputText[lastIndex] == ' ')
            newInput = inputText.substring(startIndex = 0, endIndex = lastIndex)

        val expected = "biaya pengiriman surat"
        assertEquals(expected, newInput)
    }

    @Test
    fun test_progress_bar() {
        val totalUnit = BigDecimal(43)

        val progress = flow<Int> {
            (1..43).forEach { index ->
                val numProcessed = BigDecimal(index)

                val progress = (numProcessed.divide(totalUnit, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100)))
                    .toInt()

                emit(progress)

                delay(750)
            }
        }

        runBlocking {
            progress.collect {
                println("Progress -> $it")
            }
        }
    }
}