package net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.AmbilKuitansiModel

class RoomAmbilKuitansiDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalAmbilKuitansiDataSource {

    private val dao = myRoomDatabase.getAmbilKuitansiDao()

    override suspend fun get(kavling: String, termin: String): Result<AmbilKuitansiModel?> {
        return roomOperation {
            val entity = dao.get(kavling, termin)

            entity?.toModel()
        }
    }

    override suspend fun insert(model: AmbilKuitansiModel): Result<Nothing?> {
        return roomOperation {
            val entity = model.toEntity()

            dao.insert(entity)

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