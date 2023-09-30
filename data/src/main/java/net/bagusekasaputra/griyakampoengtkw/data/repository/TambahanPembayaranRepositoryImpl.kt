package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
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

    override suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?> {
        val success = Random.nextBoolean()
        return if (success) {
            Result.success(null)
        } else {
            Result.failure(Exception("Random Error!"))
        }
    }
}