package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class MockBiayaLainRepository: BiayaLainRepository {

    override fun getAllOnline(offline: Boolean): Flow<Result<List<BiayaLain>?>> {
        return flow {
            val listBiayaLain = listOf(
                BiayaLain(jenisBiaya = "pembuatan berkas Pak Abi abudhabi", harga = 50000L, tanggal = "12/10/2022"),
                BiayaLain(jenisBiaya = "Gunting Lakban Materai", harga = 30000L, tanggal = "27/10/2022"),
                BiayaLain(jenisBiaya = "pembuatan stempel logo GKT", harga = 85000L, tanggal = "31/10/2022"),
                BiayaLain(jenisBiaya = "pembelian Map Oren dan kertas Glosy", harga = 50000L, tanggal = "03/11/2022"),
                BiayaLain(jenisBiaya = "kwitansi", harga = 10000L, tanggal = "04/11/2022"),
                BiayaLain(jenisBiaya = "Dp pembuatan APK", harga = 500000L, tanggal = "21/11/2022"),
                BiayaLain(jenisBiaya = "Service kendaraan CB", harga = 130000L, tanggal = "25/11/2022"),
                BiayaLain(jenisBiaya = "Map amplop coklat besar", harga = 50000L, tanggal = "13/12/2022"),
                BiayaLain(jenisBiaya = "pencetakan brosur 100 lembar", harga = 150000L, tanggal = "20/12/2022"),
                BiayaLain(jenisBiaya = "Dp design 3D mas fian", harga = 700000L, tanggal = "25/12/2022"),
            )

            emit(Result.success(listBiayaLain))
        }
    }

    override fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>> {
        TODO("Not yet implemented")
    }

    override fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun updateBiayaLain(
        oldBiayaLain: BiayaLain,
        newBiayaLain: BiayaLain,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}