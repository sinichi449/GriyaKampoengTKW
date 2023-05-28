package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository
import kotlin.random.Random

class AmbilKuitansiRepositoryImpl: AmbilKuitansiRepository {

    override suspend fun get(kavling: String, termin: String): Result<AmbilKuitansi?> {
        val randomSudahAmbil = Random.nextBoolean()
        val ambilKuitansi = AmbilKuitansi(kavling, termin, randomSudahAmbil)

        return Result.success(ambilKuitansi)
    }

}