package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository
import java.util.Calendar
import kotlin.random.Random

class IndenBookingRepositoryImpl: IndenBookingRepository {

    override fun getAll(): Flow<Result<List<IndenBooking>?>> {
        return flow {
            val noHp6DigitPertama = "+6281-335-"
            val jumlahIndenBooking = Random.nextInt(from = 1, until = 50)
            val tanggalLimit = Calendar.getInstance().run {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.MONTH, 1)
                set(Calendar.YEAR, 2020)

                timeInMillis
            }
            val tanggalSekarang = System.currentTimeMillis()

            val listIndenBooking = mutableListOf<IndenBooking>()
            repeat(jumlahIndenBooking) {
                val noHp3DigitTengah = Random.nextInt(from = 0, until = 999).run {
                    this.toString().padStart(3, '0')
                }
                val noHp3DigitTerakhir = Random.nextInt(from = 0, until = 999).run {
                    this.toString().padStart(3, '0')
                }
                val randomDate = Random.nextLong(from = tanggalLimit, until = tanggalSekarang).run {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = this

                    calendar.time
                }
                val randomUangDibayar = Random.nextLong(from = 1L, until = 50L) * 1_000_000L

                listIndenBooking.add(
                    IndenBooking(
                        id = it.toLong(),
                        namaCostumer = "Costumer $it",
                        tanggalDibayar = randomDate,
                        jumlahUang = randomUangDibayar,
                        noHp = "$noHp6DigitPertama-$noHp3DigitTengah-$noHp3DigitTerakhir",
                        keterangan = "-",
                    )
                )
            }

            emit(Result.success(listIndenBooking))
        }
    }
}