package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CalonPembeliRepository
import kotlin.random.Random

class CalonPembeliRepositoryImpl: CalonPembeliRepository {


    override fun getAll(): Flow<Result<List<CalonPembeli>?>> {
        return flow {
            // TODO

            val listCalonPembeli = mutableListOf<CalonPembeli>()
            val noHp9DigitDepan = "+6281335990"
            val jumlahCalonPembeli = Random.nextInt(from = 5, until = 50)

            repeat(jumlahCalonPembeli) {
                val noHp3DigitBelakang = Random.nextInt(from = 0, until = 999).run {
                    this.toString().padStart(3, '0')
                }
                listCalonPembeli.add(
                    CalonPembeli(
                        id = it.toLong(),
                        nama = "User $it",
                        noHp = "$noHp9DigitDepan$noHp3DigitBelakang",
                        lastModified = System.currentTimeMillis(),
                        keterangan = "-"
                    )
                )
            }

            emit(Result.success(listCalonPembeli))
        }
    }
}