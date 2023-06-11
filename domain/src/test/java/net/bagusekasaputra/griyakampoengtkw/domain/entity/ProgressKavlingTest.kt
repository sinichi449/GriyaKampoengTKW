package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressKavlingTest {

    private val mockRepository = MockRepository(getTestingFile(this))
    private val pembayaranRepository = mockRepository.getPembayaranRepository()

    companion object {
        const val KAVLING = ""
    }

    /**
     * Functional Tests
     */
    @Test
    fun persentaseAngsuranTest() = runTest {

    }
}