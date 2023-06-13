package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.PengingatRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengingatDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PengingatModel

class RoomPengingatDataSource(
    roomDatabase: MyRoomDatabase
): LocalPengingatDataSource {

    private val pengingatDao = roomDatabase.getPengingatDao()

    override suspend fun getAll(): Result<List<PengingatModel>?> {
        return RoomRequestHelper.doGetOperation {
            pengingatDao.getAll()?.map {
                mapPengingat(it)
            }
        }
    }

    override suspend fun getSingleById(id: Long): Result<PengingatModel?> {
        return RoomRequestHelper.doGetOperation {
            pengingatDao.getSingle(id)?.let {
                mapPengingat(it)
            }
        }
    }

    override suspend fun insert(pengingatModel: PengingatModel): Result<Long?> {
        return try {
            val id = pengingatDao.insert(mapPengingat(pengingatModel))

            Result.success(id)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun update(
        oldPengingat: PengingatModel,
        newPengingat: PengingatModel,
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            pengingatDao.update(
                id = oldPengingat.id!!,
                newTitle = newPengingat.title,
                newContent = newPengingat.content,
                newDate = newPengingat.date,
                newTime = newPengingat.time,
                newIsActive = newPengingat.isActive,
            )
        }
    }

    override suspend fun delete(pengingatModel: PengingatModel): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            pengingatDao.deleteById(pengingatModel.id!!)
        }
    }

    private fun mapPengingat(entity: PengingatRoomEntity): PengingatModel {
        return entity.let {
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

    private fun mapPengingat(model: PengingatModel): PengingatRoomEntity {
        return model.let {
            PengingatRoomEntity(
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