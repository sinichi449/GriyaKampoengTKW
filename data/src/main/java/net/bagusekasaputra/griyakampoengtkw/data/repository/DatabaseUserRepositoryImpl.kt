package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository
import kotlin.random.Random

class DatabaseUserRepositoryImpl: DatabaseUserRepository {


    override fun getAll(): Flow<Result<List<DatabaseUser>?>> {
        return flow {
            // TODO

            val listDatabaseUser = mutableListOf<DatabaseUser>()
            val noHp9DigitDepan = "+6281335990"
            val jumlahCalonPembeli = Random.nextInt(from = 5, until = 50)

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
                        keterangan = "-"
                    )
                )
            }

            emit(Result.success(listDatabaseUser))
        }
    }
}