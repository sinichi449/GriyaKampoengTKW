package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockHargaRumahIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockPembayaranRepository
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode


class PembayaranTest {

    private val pembayaranRepository = MockPembayaranRepository()
    private val hargaRumahRepository = MockHargaRumahIndenBookingRepository()

    @Test
    fun total_uang_masuk_on_specified_bulan_and_tahun() {
        val kavling = "A11"
        val bulan = 12
        val tahun = 2022
        val totalUangMasuk = 7375000L

        val uangMasuk = runBlocking {
            pembayaranRepository.getUangMasukBulanIni(kavling, bulan, tahun, DataMode.ONLINE)
                .getOrThrow()
        }

        assert(uangMasuk == totalUangMasuk)
    }

    @Test
    fun sudah_bayar_pembayaran_on_specified_bulan_and_tahun() {
        val kavling = "A11"
        val bulan = 12
        val tahun = 2022

        val adaPembayaran = runBlocking {
            pembayaranRepository.sudahBayarAngsuran(kavling, bulan, tahun, DataMode.ONLINE)
                .getOrThrow()
        }

        assert(adaPembayaran == true)
    }

    @Test
    fun next_sequence_of_pembayaran_given_jenis_pembayaran_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val requestedJenisPembayaran = Pembayaran.JenisPembayaran.DP
        val correctNextSequence = "9"

        val pembayarans = runBlocking {
            pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
        }
        val nextSequence = Pembayaran.nextPembayaranSequence(pembayarans, requestedJenisPembayaran)

        assert(correctNextSequence == nextSequence)
    }

    @Test
    fun hitung_total_uang_masuk_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val correctTotalUangMasuk = 38_005_000L

        val pembayarans = runBlocking {
            pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
        }
        val totalUangMasuk = Pembayaran.hitungTotalUangMasuk(pembayarans)

        assert(totalUangMasuk == correctTotalUangMasuk)
    }

    @Test
    fun urutan_pembayaran_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val orderedListTermin = listOf("ITJ 1", "DP 1", "DP 2", "DP 3", "DP 4", "DP 5",
            "DP 6", "DP 7", "DP 8")

        val sortedPembayaran = runBlocking {
            val pembayarans = pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()!!
            val hargaRumah = hargaRumahRepository.get(keyId, DataMode.ONLINE).getOrThrow()!!

            val shuffledPembayaran = pembayarans.shuffled()

            Pembayaran.maskPembayaran(shuffledPembayaran, hargaRumah,
                onCekFotoPembayaran = { false },
                onCekSudahAmbilKuitansi = { false },
            )
        }
        val listTermin = sortedPembayaran.map { it.termin }

        assert(listTermin == orderedListTermin)
    }

    @Test
    fun persentase_pembayaran_inden_booking_correct() {
        val keyId = "3053d174-4b9b-437c-96aa-68fd44fa0fef"
        val persentasePembayaranTerakhir = BigDecimal(0.135732143)
            .setScale(4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100L))
            .toDouble()

        val maskedPembayaran = runBlocking {
            val pembayarans = pembayaranRepository.getAllFromIndenBooking(keyId).getOrThrow()
            val hargaRumah = hargaRumahRepository.get(keyId, DataMode.ONLINE).getOrThrow()

            Pembayaran.maskPembayaran(pembayarans!!, hargaRumah!!,
                onCekFotoPembayaran = { false },
                onCekSudahAmbilKuitansi = { false },
            )
        }
        val lastPembayaran = maskedPembayaran.last()

        assert(lastPembayaran.presentase == persentasePembayaranTerakhir)
    }
}