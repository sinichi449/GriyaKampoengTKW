package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class PengingatRepositoryImpl: PengingatRepository {

    override fun getAll(): Flow<Result<List<Pengingat>?>> {
        return flow {

        }
    }

    override fun getSingleById(id: Long): Flow<Result<Pengingat?>> {
        return flow {

        }
    }

    override fun insert(pengingat: Pengingat): Flow<Result<Long?>> {
        return flow {

        }
    }

    override fun update(oldPengingat: Pengingat, newPengingat: Pengingat): Flow<Result<Nothing?>> {
        return flow {

        }
    }

    override fun delete(pengingat: Pengingat): Flow<Result<Nothing?>> {
        return flow {

        }
    }
}