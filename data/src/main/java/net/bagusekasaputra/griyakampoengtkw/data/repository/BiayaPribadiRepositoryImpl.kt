package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaPribadiRepository
import java.util.Calendar
import kotlin.random.Random

class BiayaPribadiRepositoryImpl: BiayaPribadiRepository {

    override fun getAll(): Flow<Result<List<BiayaPribadi>?>> {
        return flow {
            val jumlahData = Random.nextInt(from = 1, until = 50)

            val listBiayaPribadi = mutableListOf<BiayaPribadi>()
            repeat(jumlahData) {
                val randomTanggal = Calendar.getInstance().run {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.MONTH, 10)
                    set(Calendar.YEAR, 2020)

                    val tanggalLimit = this.timeInMillis
                    val tanggalSekarang = System.currentTimeMillis()

                    Random.nextLong(from = tanggalLimit, until = tanggalSekarang)
                }.let {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it

                    calendar.time
                }
                val randomHarga = Random.nextLong(from = 1L, until = 1_000L) * 1_000L

                listBiayaPribadi.add(
                    BiayaPribadi(
                        jenisBiaya = "BP $it",
                        tanggal = randomTanggal,
                        harga = randomHarga,
                    )
                )
            }

            emit(Result.success(listBiayaPribadi))
        }
    }

}