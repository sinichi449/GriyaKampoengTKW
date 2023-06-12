package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.firstOrThrow
import net.bagusekasaputra.griyakampoengtkw.domain.misc.InvalidTimeFrameBaselinePembayaranException
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MockRepository.Companion.DEFAULT_DATA_MODE
import net.bagusekasaputra.griyakampoengtkw.domain.util.getTestingFile
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class BaselinePembayaranTest {

    private val mockRepository = MockRepository(getTestingFile(this))
    private val baselineRepository = mockRepository.getBaselinePembayaranRepository()
    private val pembayaranRepository = mockRepository.getPembayaranRepository()


    @Test
    fun tanggalSelesaiAngsuran_timeFrameTahun_shouldCorrect() {
        val tanggalPembelian = "03/06/2023".toDate()
        val timeFrameTahun = 4
        val tanggalSelesai = BaselinePembayaran.hitungTanggalAngsuranSelesai(
            tanggalPembelian, BaselinePembayaran.OPSI_TIMEFRAME_TAHUN, timeFrameTahun
        )

        Assert.assertEquals("03/06/2027", tanggalSelesai.toSlashedString())
    }

    @Test
    fun tanggalSelesaiAngsuran_timeFrameBulan_shouldCorrect() {
        val tanggalPembelian = "03/06/2023".toDate()
        val timeFrameBulan = 15
        val tanggalSelesai = BaselinePembayaran.hitungTanggalAngsuranSelesai(
            tanggalPembelian, BaselinePembayaran.OPSI_TIMEFRAME_BULAN, timeFrameBulan
        )

        Assert.assertEquals("03/09/2024", tanggalSelesai.toSlashedString())
    }

    @Test
    fun hitungSisaBlmBayarBulanIni_shouldBasedOnBulanAngsuranOrInvoice() = runTest {
        val kavling = "A12"
        val bulanSekarang = 6
        val tahunSekarang = 2023

        var pembayaranList = pembayaranRepository.getAllPembayaran(kavling, DEFAULT_DATA_MODE)
            .firstOrThrow()!!
        val baselinePembayaran = baselineRepository.get(kavling, DEFAULT_DATA_MODE)
            .firstOrThrow()!!

        /*
       Kav. A12
       Angsuran    : 6,500,000

       No.           Termin        Invoice        Tanggal       Jumlah Uang
       ------------------------------------------------------------------------
       1              ITJ 1          12/2022        12/12/2022     6,500,000
       2              DP 1           01/2023        15/01/2023     6,450,000
       3              DP 2           02/2023        12/02/2023     6,550,000
       4              DP 3           03/2023        12/03/2023     6,500,000
       5              DP 4           04/2023        09/04/2023     6,500,000
       6              DP 5           05/2023        14/05/2023     6,000,000
        */

        // Set DP 5 to tanggal 14/06/2023
        pembayaranList = pembayaranList.toMutableList().apply {
            replaceAll { pembayaran ->
                if (pembayaran.termin == "DP 5") {
                    pembayaran.copy(tanggal = "14/06/2023")
                } else {
                    pembayaran
                }
            }
        }
        /*
        ....
        6              DP 5           05/2023        14/06/2023     6,000,000
         */

        Assert.assertEquals(
            6_500_000L,
            baselinePembayaran.hitungSisaBlmBayarBulanIni(pembayaranList, bulanSekarang, tahunSekarang)
        )
    }

    @Test
    fun givenTimeFrameIsZero_whenHitungAngsuranPerBulan_shouldThrowArithmeticException() {
        val hargaKavling = HargaKavling(
            kavlingKode = "B3",
            harga = "230,000,000",
            tambahanLuas = "0",
        )

        Assert.assertThrows(InvalidTimeFrameBaselinePembayaranException::class.java) {
            BaselinePembayaran.hitungAngsuranPerBulan(
                hargaKavling = hargaKavling,
                opsiTimeFrame = BaselinePembayaran.OPSI_TIMEFRAME_BULAN,
                timeFrame = 0,
            )
        }
    }

    @Test
    fun givenOpsiTimeFrameIsInvalid_whenHitungAngsuranBulan_shouldThrowIllegalArgumentException() {
        val hargaKavling = HargaKavling(
            kavlingKode = "B3",
            harga = "230,000,000",
            tambahanLuas = "0"
        )

        Assert.assertThrows(IllegalArgumentException::class.java) {
            BaselinePembayaran.hitungAngsuranPerBulan(
                hargaKavling = hargaKavling,
                opsiTimeFrame = 999,
                timeFrame = 10,
            )
        }
    }
}