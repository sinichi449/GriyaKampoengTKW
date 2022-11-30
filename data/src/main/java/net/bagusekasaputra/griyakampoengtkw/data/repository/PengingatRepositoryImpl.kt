package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengingatDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PengingatModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class PengingatRepositoryImpl(
    private val localPengingatDataSource: LocalPengingatDataSource,
): PengingatRepository {

    override fun getAll(): Flow<Result<List<Pengingat>?>> {
        return flow {
            val localResult = localPengingatDataSource.getAll()

            emit(
                DataUtil.mapListResult(
                    originResult = localResult,
                    targetMapper = ::mapPengingat,
                )
            )
        }
    }

    override fun getSingleById(id: Long): Flow<Result<Pengingat?>> {
        return flow {
            val localResult = localPengingatDataSource.getSingleById(id)

            emit(
                DataUtil.mapSingleResult(
                    originResult = localResult,
                    targetMapper = ::mapPengingat,
                )
            )
        }
    }

    override fun insert(pengingat: Pengingat): Flow<Result<Long?>> {
        return flow {
            val localResult = localPengingatDataSource.insert(mapPengingat(pengingat))

            emit(localResult)
        }
    }

    override fun update(oldPengingat: Pengingat, newPengingat: Pengingat): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localPengingatDataSource.update(
                oldPengingat = mapPengingat(oldPengingat),
                newPengingat = mapPengingat(newPengingat),
            )

            emit(localResult)
        }
    }

    override fun delete(pengingat: Pengingat): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localPengingatDataSource.delete(mapPengingat(pengingat))

            emit(localResult)
        }
    }

    private fun mapPengingat(pengingatModel: PengingatModel): Pengingat {
        return pengingatModel.let {
            Pengingat(
                id = it.id,
                title = it.title,
                content = it.content,
                date = it.date,
                time = it.time,
                isActive = it.isActive,
            )
        }
    }

    private fun mapPengingat(pengingat: Pengingat): PengingatModel {
        return pengingat.let {
            PengingatModel(
                id = it.id,
                title = it.title,
                content = it.content,
                date = it.date,
                time = it.time,
                isActive = it.isActive,
            )
        }
    }
}