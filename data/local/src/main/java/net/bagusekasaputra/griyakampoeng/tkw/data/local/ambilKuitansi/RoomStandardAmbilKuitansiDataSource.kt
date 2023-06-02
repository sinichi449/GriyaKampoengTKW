package net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.StandardAmbilKuitansiModel

class RoomStandardAmbilKuitansiDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalStandardAmbilKuitansiDataSource {

    private val dao = myRoomDatabase.getStandardAmbilKuitansiDao()

    override suspend fun get(kavling: String, termin: String): Result<StandardAmbilKuitansiModel?> {
        return roomOperation {
            val entity = dao.get(kavling, termin)

            entity?.toModel()
        }
    }

    override suspend fun insert(model: StandardAmbilKuitansiModel): Result<Nothing?> {
        return roomOperation {
            val entity = model.toEntity()

            dao.insert(entity)

            null
        }
    }

    override suspend fun update(model: StandardAmbilKuitansiModel): Result<Nothing?> {
        return roomOperation {
            // Delete then insert
            val oldEntity = dao.get(model.kavling, model.termin)
            if (oldEntity != null) {
                dao.delete(model.kavling, model.termin)
            }

            dao.insert(model.toEntity())

            null
        }
    }

    override suspend fun delete(kavling: String, termin: String): Result<Nothing?> {
        return roomOperation {
            dao.delete(kavling, termin)

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            dao.deleteAll()

            null
        }
    }
}