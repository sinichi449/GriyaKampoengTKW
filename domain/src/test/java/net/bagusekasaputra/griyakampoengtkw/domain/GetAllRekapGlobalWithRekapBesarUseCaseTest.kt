package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal.GetAllRekapGlobalWithRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.mockRepository.*
import org.junit.Test

class GetAllRekapGlobalWithRekapBesarUseCaseTest {

    private val dataDiriRepository = MockDataDiriRepository()
    private val hargaKavlingRepository = MockHargaKavlingRepository()
    private val pembayaranRepository = MockPembayaranRepository()
    private val feeMarketingRepository = MockFeeMarketingRepository()
    private val biayaMarketingRepository = MockBiayaMarketingRepository()
    private val biayaLainRepository = MockBiayaLainRepository()

    @Test
    fun test_my_usecase() {
        val useCase = GetAllRekapGlobalWithRekapBesarAsyncUseCase(dataDiriRepository, hargaKavlingRepository, pembayaranRepository, feeMarketingRepository, biayaMarketingRepository, biayaLainRepository)

        val request = GetAllRekapGlobalWithRekapBesarAsyncUseCase
            .Request(
                listOf("A2", "A3", "A4")
            )

        runBlocking {
            useCase.execute(request).collect { result ->
                result.onSuccess { rekapGlobalWithBesar ->
                    repeat(50) { print("=") }
                    println()
                    println("REKAP GLOBAL PER KAVLING")
                    println()
                    rekapGlobalWithBesar?.listRekapGlobal?.forEach { rg ->
                        println("Kavling ${rg.noKavling}")
                        repeat(times = 50) { print("-") }
                        println()
                        println("Nama Costumer      : ${rg.namaCostumer}")
                        println("Tanggal Pembelian  : ${rg.tanggalPembelian}")
                        println("Harga              : ${rg.parsedHarga}")
                        println("Uang Masuk         : ${rg.parsedJumlahUangMasuk}")
                        println("Sisa Pembayaran    : ${rg.parsedSisaPembayaran}")
                        println("Persentase         : ${rg.persentase}")
                        repeat(times = 50) { print("-") }
                        println()
                    }

                    println()
                    repeat(50) { print("=") }
                    println()
                    println("REKAP BESAR")
                    println()
                    println("Total Uang Masuk   : ${rekapGlobalWithBesar?.rekapBesar?.parsedTotalUangMasuk}")
                    println("Total Pengeluaran  : ${rekapGlobalWithBesar?.rekapBesar?.parsedTotalPengeluaran}")
                    println("   - Fee Marketing : ${rekapGlobalWithBesar?.rekapBesar?.parsedTotalFeeMarketing}")
                    println("   - B. Marketing  : ${rekapGlobalWithBesar?.rekapBesar?.parsedTotalBiayaMarketing}")
                    println("   - Biaya Lain2   : ${rekapGlobalWithBesar?.rekapBesar?.parsedTotalBiayaLain}")
                    repeat(times = 25) { print("-") }
                    println()
                    println("Sisa Uang          : ${rekapGlobalWithBesar?.rekapBesar?.parsedSisaUang}")
                }

                result.onFailure {
                    println("Fail -> ${it.message}")
                }
            }
        }
    }
}