package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.delay
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository
import kotlin.random.Random

class TambahanPembayaranRepositoryImpl: TambahanPembayaranRepository {
    override suspend fun getAllByKavling(kavling: String): Result<List<TambahanPembayaran>?> {
        Log.d("TAMBAHAN_PEMBAYARAN", "Request for kavling: $kavling")
        return Result.success(listOf(
            TambahanPembayaran(
                kavling = kavling,
                kategori = TambahanPembayaran.Kategori.LUASAN,
                tanggal = "01/01/2029".toDate(),
                jumlahUang = 12_000_000,
                keterangan = "DP 1 tambahan 40 m2",
                sudahIsiFoto = false
            ),
            TambahanPembayaran(
                kavling = kavling,
                kategori = TambahanPembayaran.Kategori.PEMBANGUNAN,
                tanggal = "01/01/2029".toDate(),
                jumlahUang = 12_000_000,
                keterangan = "Meja dapur L",
                sudahIsiFoto = true,
            )
        ))
    }

    override suspend fun getById(kavling: String, id: String): Result<TambahanPembayaran?> {
        val success = Random.nextBoolean()
        delay(3000L)
        return if (success) {
            Result.success(
                TambahanPembayaran(
                    kavling = kavling,
                    kategori = TambahanPembayaran.Kategori.LUASAN,
                    tanggal = "01/01/2029".toDate(),
                    jumlahUang = 12_000_000,
                    keterangan = "DP 1 tambahan 40 m2",
                    sudahIsiFoto = false
                )
            )
        } else {
            Result.failure(Exception("Random Error!"))
        }
    }

    override suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?> {
        val success = Random.nextBoolean()
        delay(5000L)
        return if (success) {
            Result.success(null)
        } else {
            Result.failure(Exception("Random Error!"))
        }
    }

    override suspend fun update(id: String, newData: TambahanPembayaran): Result<Nothing?> {
        val success = Random.nextBoolean()
        delay(5000L)
        return if (success) {
            Result.success(null)
        } else {
            Result.failure(Exception("Random Error!"))
        }
    }
}