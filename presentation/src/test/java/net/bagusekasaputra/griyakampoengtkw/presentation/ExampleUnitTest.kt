package net.bagusekasaputra.griyakampoengtkw.presentation

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
}