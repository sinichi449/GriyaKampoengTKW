package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class MockBiayaLainRepository: BiayaLainRepository {

    override fun getAll(offline: Boolean): Flow<Result<List<BiayaLain>?>> {
        return flow {
            val minBiayaLain = 1L
            val maxBiayaLain = 50L

            val listBiayaLain = listOf(
                BiayaLain(jenisBiaya = "mock 1", harga = MockUtils.getRandomDuwitValue(minBiayaLain, maxBiayaLain), tanggal = "13/12/2022"),
                BiayaLain(jenisBiaya = "mock 2", harga = MockUtils.getRandomDuwitValue(minBiayaLain, maxBiayaLain), tanggal = "13/12/2022"),
                BiayaLain(jenisBiaya = "mock 3", harga = MockUtils.getRandomDuwitValue(minBiayaLain, maxBiayaLain), tanggal = "13/12/2022"),
                BiayaLain(jenisBiaya = "mock 4", harga = MockUtils.getRandomDuwitValue(minBiayaLain, maxBiayaLain), tanggal = "13/12/2022"),
                BiayaLain(jenisBiaya = "mock 5", harga = MockUtils.getRandomDuwitValue(minBiayaLain, maxBiayaLain), tanggal = "13/12/2022"),
            )

            emit(Result.success(listBiayaLain))
        }
    }

    override fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>> {
        TODO("Not yet implemented")
    }

    override fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing>?> {
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