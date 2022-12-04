package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class BiayaLainRepositoryImpl: BiayaLainRepository {

    override fun getAll(offline: Boolean): Flow<Result<List<BiayaLain>?>> {
        return flow {
            emit(Result.success(null))
        }
    }

    override fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>> {
        return flow {

        }
    }

    override fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing>?> {
        return flow {

        }
    }

    override fun updateBiayaLain(
        oldBiayaLain: BiayaLain,
        newBiayaLain: BiayaLain,
    ): Flow<Result<Nothing?>> {
        return flow {

        }
    }

    override fun deleteBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>> {
        return flow {

        }
    }
}