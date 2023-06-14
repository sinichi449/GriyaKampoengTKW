package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.dateToTimeMillis
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.timeMillisToDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMaterialRepository
import kotlin.random.Random

class BiayaMaterialRepositoryImpl: BiayaMaterialRepository {

    override fun getAsFlow(dataMode: DataMode): Flow<Result<BiayaMaterial?>> {
        return flow {
            val kavlingList = Kavling.getGriyaKavlingList()
            val itemSize = Random.nextInt(from = 10, until = 50)
            val mockLists = buildList {
                repeat(itemSize) { index ->
                    val tanggalBeli = Random.nextLong(
                        from = "14/06/2020".dateToTimeMillis(),
                        until = "14/06/2023".dateToTimeMillis(),
                    )
                    val hargaItem = Random.nextLong(from = 1L, until = 3_000L) * 1_000L
                    val pcs = Random.nextInt(from = 1, until = 100)

                    add(BiayaMaterial(
                        untukKavling = kavlingList.random(),
                        tanggalBeli = tanggalBeli.timeMillisToDate(),
                        namaItem = "Item ${index + 1}",
                        hargaPerItem = hargaItem,
                        pcs = pcs,
                        buktiPembayaran = if (Random.nextBoolean()) "https://www.google.com" else "",
                    ))
                }
            }

            mockLists.forEach {
                emit(Result.success(it))
                delay(Random.nextLong(from = 50L, until = 1000L))
            }
        }
    }

    override suspend fun getByKeyId(keyId: String, dataMode: DataMode): Result<BiayaMaterial?> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(biayaMaterial: BiayaMaterial): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(keyId: String, newBiayaMaterial: BiayaMaterial): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(keyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}