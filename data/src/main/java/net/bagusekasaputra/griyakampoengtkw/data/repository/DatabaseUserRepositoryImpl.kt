package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository
import kotlin.random.Random

class DatabaseUserRepositoryImpl: DatabaseUserRepository {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun getAll(): Flow<Result<List<DatabaseUser>?>> {
        return flow {
            // TODO

            val listDatabaseUser = mutableListOf<DatabaseUser>()
            val noHp9DigitDepan = "+6281335990"
            val jumlahCalonPembeli = Random.nextInt(from = 5, until = 50)
            val usernameTiktok = "rumah_dijual_gondanglegi"
            val listLokasiIndo = listOf("Malang", "Madiun", "Jombang", "Blitar", "Surabaya", "Banyuwangi", "Jayapura", "Makassar", "Jakarta")
            val negaraBekerjaList = ArrayList<String>().apply {
            add("Hongkong")
            add("Macau")
            add("Taiwan")
            add("Jepang")
            add("Singapore")
            add("Malaysia")
            add("Arab Saudi")
            add("Abu Dhabi")
            add("Bangladesh")
            add("Kamboja")
            add("Brunei Darussalam")
            add("Korea")
        }

            repeat(jumlahCalonPembeli) {
                val noHp3DigitBelakang = Random.nextInt(from = 0, until = 999).run {
                    this.toString().padStart(3, '0')
                }
                listDatabaseUser.add(
                    DatabaseUser(
                        id = it.toLong(),
                        nama = "User $it",
                        noHp = "$noHp9DigitDepan$noHp3DigitBelakang",
                        lastModified = System.currentTimeMillis(),
                        keterangan = "-",
                        _usernameTiktok = usernameTiktok,
                        tanggal = System.currentTimeMillis().run {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = this

                            calendar.time
                        },
                        lokasiIndo = Random.nextInt(from = 0, until = listLokasiIndo.size-1).run {
                            listLokasiIndo[this]
                        },
                        negaraBekerja = Random.nextInt(from = 0, until = negaraBekerjaList.size-1).run {
                            negaraBekerjaList[this]
                        },
                    )
                )
            }

            emit(Result.success(listDatabaseUser))
        }
    }
}