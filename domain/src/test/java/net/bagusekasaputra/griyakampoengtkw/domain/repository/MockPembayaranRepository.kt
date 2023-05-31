package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

class MockPembayaranRepository: PembayaranRepository {

    private val mapPembayarans = mapOf(
        "A11" to listOf(
            Pembayaran(termin = "ITJ 1", tanggal = "09/12/2022", jumlahUangDibayar = "5,000,000", keterangan = "ITJ", timeMillis = 1670575902439),
            Pembayaran(termin = "DP 1", tanggal = "19/12/2022", jumlahUangDibayar = "1,000,000", keterangan = "Dp1", timeMillis = 1671422391736),
            Pembayaran(termin = "DP 2", tanggal = "19/12/2022", jumlahUangDibayar = "1,375,000", keterangan = "-", timeMillis = 1671459285668)
        )
    )

    override fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            emit(Result.success(mapPembayarans))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override fun getBatchFromRemoteBackup(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        TODO("Not yet implemented")
    }

    override fun getAllPembayaran(
        kavlingKode: String,
        dataMode: DataMode
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            emit(Result.success(mapPembayarans[kavlingKode]))
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        return flow {
            emit(Result.success(mapPembayarans[kavlingKode]))
        }
    }

    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun sudahBayarAngsuran(
        kavlingKode: String,
        bulan: Int,
        tahun: Int,
        dataMode: DataMode
    ): Result<Boolean?> {
        val pembayarans = mapPembayarans[kavlingKode]

        return if (!pembayarans.isNullOrEmpty()) {
            Result.success(Pembayaran.adakahPembayaranBulanDanTahunIni(
                pembayarans, bulan, tahun,
            ))
        } else {
            Result.success(false)
        }
    }

    override suspend fun getUangMasukBulanIni(
        kavlingKode: String,
        bulan: Int,
        tahun: Int,
        dataMode: DataMode
    ): Result<Long> {
        val pembayarans = mapPembayarans[kavlingKode]

        return if (!pembayarans.isNullOrEmpty()) {
            Result.success(Pembayaran.uangMasukPadaBulanDanTahunIni(
                pembayarans, bulan, tahun
            ))
        } else {
            Result.success(0L)
        }
    }

    override suspend fun refreshCache(kavlings: List<String>): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<Pembayaran>?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        pembayaran: Pembayaran
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}