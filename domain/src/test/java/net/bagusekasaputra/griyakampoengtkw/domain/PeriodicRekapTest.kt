package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapBesar
import net.bagusekasaputra.griyakampoengtkw.domain.mockRepository.*
import org.junit.Test
import java.util.*

class PeriodicRekapTest {

    private val mockDataDiriRepo = MockDataDiriRepository()
    private val mockPembayaranRepo = MockPembayaranRepository()
    private val mockHargaKavlingRepo = MockHargaKavlingRepository()
    private val mockFeeMarketingRepo = MockFeeMarketingRepository()
    private val mockBiayaMarketingRepo = MockBiayaMarketingRepository()
    private val mockBiayaLainRepo = MockBiayaLainRepository()

    private val getRekapBesarUseCase = GetRekapBesarAsyncUseCase(
        pembayaranRepository = mockPembayaranRepo,
        hargaKavlingRepository = mockHargaKavlingRepo,
        feeMarketingRepository = mockFeeMarketingRepo,
        biayaMarketingRepository = mockBiayaMarketingRepo,
        biayaLainRepository = mockBiayaLainRepo,
    )

    @Test
    fun test_list_rekap_global() {
        val getListRekapGlobalAsyncUseCase = GetListRekapGlobalAsyncUseCase(
            dataDiriRepository = mockDataDiriRepo,
            pembayaranRepository = mockPembayaranRepo,
            hargaKavlingRepository = mockHargaKavlingRepo,
        )
        val request = GetListRekapGlobalAsyncUseCase.Request(Kavling.getGriyaKavlingList())

        runBlocking {
            getListRekapGlobalAsyncUseCase.execute(request).collect { result ->
                result.onSuccess { listRekapGlobal ->
                    listRekapGlobal?.forEach {
                        println("=====================================================================")
                        println("No Kavling         : ${it.noKavling}")
                        println("Nama Costumer      : ${it.namaCostumer}")
                        println("Tanggal Pembelian  : ${it.tanggalPembelian}")
                        println("Harga Kavling      : ${it.parsedHarga}")
                        println("Total Uang         : ${it.parsedJumlahUangMasuk}")
                        println()
                    }
                }
            }
        }
    }

    @Test
    fun test_pembayaran_repo() {
        runBlocking {
            val request = GetRekapBesarAsyncUseCase.Request(
                kavlingList = Kavling.getGriyaKavlingList(),
                periode = PeriodeRekap.SEMUA,
                startDate = null,
                endDate = null,
            )

            val listKavling = Kavling.getGriyaKavlingList()
            mockPembayaranRepo.getBatch(listKavling).collect { result ->
                result.onSuccess { mapPembayaran ->
                    mapPembayaran?.keys?.forEach { kavling ->
                        println("==========================================================================")
                        println(kavling)
                        println()
                        mapPembayaran[kavling]?.forEach {
                            println("---------------------------------------------------------------------")
                            println("Termin     : ${it.termin}")
                            println("Tanggal    : ${it.tanggal}")
                            println("Uang Masuk : ${it.jumlahUangDibayar}")
                            println("---------------------------------------------------------------------")
                        }
                        println()
                    }
                }
            }
        }
    }

    @Test
    fun test_get_all_rekap_besar() {
        runBlocking {
            val request = GetRekapBesarAsyncUseCase.Request(
                kavlingList = Kavling.getGriyaKavlingList(),
                periode = PeriodeRekap.SEMUA,
                startDate = null,
                endDate = null,
            )
            getRekapBesarUseCase.execute(request).collect { result ->
                result.onSuccess {
                    it?.print()
                }
            }
        }
    }

    @Test
    fun test_get_tahun_ini_rekap_besar() {
        runBlocking {
            val request = GetRekapBesarAsyncUseCase.Request(
                kavlingList = Kavling.getGriyaKavlingList(),
                periode = PeriodeRekap.TAHUN_INI,
                startDate = null,
                endDate = null,
            )
            getRekapBesarUseCase.execute(request).collect { result ->
                result.onSuccess {
                    it?.print()
                }
            }
        }
    }

    @Test
    fun test_get_bulan_ini_rekap_besar() {
        runBlocking {
            val request = GetRekapBesarAsyncUseCase.Request(
                kavlingList = Kavling.getGriyaKavlingList(),
                periode = PeriodeRekap.BULAN_INI,
                startDate = null,
                endDate = null,
            )

            val rangeRekap = getRekapBesarUseCase.getMonthlyRangeDate()
            println("Range rekap : ${rangeRekap[0].toSlashedString()} - ${rangeRekap[1].toSlashedString()}")
            println()

            getRekapBesarUseCase.execute(request).collect { result ->
                result.onSuccess { rekapBesar ->
                    rekapBesar?.print()
                }
            }
        }
    }

    private fun RekapBesar.print() {
        println("======================================================================")
        println("Rekap Besar")
        println("Sisa Uang          : ${this.parsedSisaUang}")
        println()
        println("-----------------------------------------------------------------------")
        println("Pemasukan")
        println()
        println("Uang Masuk         : ${this.parsedTotalUangMasuk}")
        println("Sisa Pembayaran    : ${this.parsedTotalSisaBelumBayar}")
        println("-----------------------------------------------------------------------")
        println("Pengeluaran")
        println()
        println("Fee Marketing      : ${this.parsedTotalFeeMarketing}")
        println("Biaya Marketing    : ${this.parsedTotalBiayaMarketing}")
        println("Biaya Lainnya      : ${this.parsedTotalBiayaLain}")
        println("======================================================================")
    }

    private fun String.toDate(): Date {
        return this.split("/").let {
            val tanggal = it[0].toInt()
            val bulan = it[1].toInt() - 1
            val tahun = it[2].toInt()

            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, tanggal)
                set(Calendar.MONTH, bulan)
                set(Calendar.YEAR, tahun)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
    }

    private fun Date.toSlashedString(): String {
        val calendar = Calendar.getInstance().apply { time = this@toSlashedString }
        val tanggal = calendar.get(Calendar.DAY_OF_MONTH)
        val bulan = calendar.get(Calendar.MONTH) + 1
        val tahun = calendar.get(Calendar.YEAR)

        return "${tanggal}/${bulan}/${tahun}"
    }

    private fun Date.isWithinRange(startDate: Date, endDate: Date)
            = !(this.before(startDate) || this.after(endDate))
}