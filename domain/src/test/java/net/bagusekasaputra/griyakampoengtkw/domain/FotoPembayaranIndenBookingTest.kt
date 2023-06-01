package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockFotoPembayaranIndenBookingRepository
import org.junit.Test

class FotoPembayaranIndenBookingTest {

    private val fotoPembayaranIndenBookingRepository = MockFotoPembayaranIndenBookingRepository()

    @Test
    fun nama_file_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val termin = "DP 1"
        val namaFile = "DP_1--3053d174-4b9b-437c-96aa-68fd44fa0fef.png"

        val fotoPembayaranIndenBooking = runBlocking {
            fotoPembayaranIndenBookingRepository.get(keyId, termin).getOrThrow()
        }

        assert(fotoPembayaranIndenBooking?.filename == namaFile)
    }
}