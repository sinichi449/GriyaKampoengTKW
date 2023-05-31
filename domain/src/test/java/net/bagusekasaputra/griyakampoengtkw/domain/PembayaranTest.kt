package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockPembayaranRepository
import org.junit.Test


class PembayaranTest {

    private val pembayaranRepository = MockPembayaranRepository()

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
}