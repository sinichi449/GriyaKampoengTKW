package net.bagusekasaputra.griyakampoengtkw.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekapGlobal.GetAllRekapGlobalUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import org.junit.Test

class GetAllRekapGlobalUseCaseTest {

    private val mockDataDiriRepository = object : DataDiriRepository {
        override fun getDataDiri(kavlingKode: String, offline: Boolean): Flow<Result<DataDiri?>> {
            return flow {
                val listDataDiri = listOf(
                    DataDiri("Andi Setya Budi", "", "", "", "", "", ""),
                    DataDiri("Iwan Ferdiyanto", "", "", "", "", "", ""),
                    DataDiri("Norma Fiki Sugiarta", "", "", "", "", "", ""),
                )
                val dataDiri: DataDiri? = when (kavlingKode) {
                    "A2" -> listDataDiri[0]
                    "A3" -> listDataDiri[1]
                    "A4" -> listDataDiri[2]
                    else -> null
                }

                emit(Result.success(dataDiri))
            }
        }

        override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

        override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

    }

    private val mockPembayaranRepository = object : PembayaranRepository {
        override fun getAllPembayaran(
            kavlingKode: String,
            offline: Boolean,
        ): Flow<Result<List<Pembayaran>?>> {
            return flow {
                val timeMillis = System.currentTimeMillis()

                val listPembayaranA2 = listOf(
                    Pembayaran("ITJ 1", "06/05/2021", "40,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 1", "07/05/2021", "40,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 2", "08/05/2021", "50,000,000", "", 0.0, "", "-", timeMillis, true),
                )

                val listPembayaranA3 = listOf(
                    Pembayaran("ITJ 1", "01/05/2021", "15,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 1", "01/05/2021", "15,000,000", "", 0.0, "", "-", timeMillis, true),
                )

                val listPembayaranA4 = listOf(
                    Pembayaran("ITJ 1", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 1", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 2", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 3", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                    Pembayaran("DP 4", "13/07/2022", "22,087,227", "", 0.0, "", "-", timeMillis, true),
                )

                val listPembayaran = when (kavlingKode) {
                    "A2" -> listPembayaranA2
                    "A3" -> listPembayaranA3
                    "A4" -> listPembayaranA4
                    else -> null
                }

                emit(Result.success(listPembayaran))
            }
        }

        override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
            TODO("Not yet implemented")
        }

        override fun addPembayaran(
            kavlingKode: String,
            hargaKavling: Long,
            pembayaran: Pembayaran,
        ): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

        override fun updatePembayaran(
            kavlingKode: String,
            oldPembayaran: Pembayaran,
            newPembayaran: Pembayaran,
        ): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

        override fun deletePembayaranByTermin(
            kavlingKode: String,
            termin: String,
        ): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

        override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

    }

    private val mockHargaKavlingRepository = object : HargaKavlingRepository {
        override fun getHargaKavling(
            kavlingKode: String,
            offline: Boolean,
        ): Flow<Result<HargaKavling?>> {
            return flow {
                val hargaKavling = when (kavlingKode) {
                    "A2" -> HargaKavling("A2", "250,000,000", "0")
                    "A3" -> HargaKavling("A3", "210,000,000", "0")
                    "A4" -> HargaKavling("A4", "230,000,000", "0")
                    else -> null
                }

                emit(Result.success(hargaKavling))
            }
        }

        override fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<HargaKavling> {
            TODO("Not yet implemented")
        }

        override fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>> {
            TODO("Not yet implemented")
        }

        override fun deleteHargaKavling(kavlingKode: String): Flow<Result<Nothing?>> {
            TODO("Not yet implemented")
        }

    }

    @Test
    fun test_rekap() {
        val getAllRekapGlobalUseCase = GetAllRekapGlobalUseCase(
            dataDiriRepository = mockDataDiriRepository,
            pembayaranRepository = mockPembayaranRepository,
            hargaKavlingRepository = mockHargaKavlingRepository,
        )

        runBlocking {
            val request = GetAllRekapGlobalUseCase.Request(listOf("A2", "A3", "A4"))
            getAllRekapGlobalUseCase.execute(request).collect { result ->
                result.onSuccess { listRekapGlobal ->
                    if (listRekapGlobal != null) {
                        listRekapGlobal.forEach {
                            println("-------------------------------------------------------------")
                            println("Nama\t\t:\t${it.namaCostumer}")
                            println("Kavling\t\t:\t${it.noKavling}")
                            println("Tanggal Pembelian\t:\t${it.tanggalPembelian}")
                            println("Harga\t\t:\t:${it.parsedHarga}")
                            println("Jumlah Uang\t:\t${it.parsedJumlahUangMasuk}")
                            println("Sisa Pembayaran\t:\t${it.parsedSisaPembayaran}")
                            println("Persentase\t\t:\t${it.parsedPersentase}")
                        }
                    } else {
                        println("Rekap global is null")
                    }
                }

                result.onFailure {
                    println("Failure : ${it.message}")
                }
            }
        }
    }
}