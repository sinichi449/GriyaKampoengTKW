package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toIndenBookingAmbilKuitansiEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toIndenBookingAmbilKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel

class RoomIndenBookingAmbilKuitansiDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalIndenBookingAmbilKuitansiDataSource {

    private val ambilKuitansiDao by lazy {
        myRoomDatabase.getIndenBookingAmbilKuitansiDao()
    }

    override suspend fun get(
        keyId: String,
        termin: String
    ): Result<IndenBookingAmbilKuitansiModel?> {
        return roomOperation {
            val entity = ambilKuitansiDao.get(keyId, termin)

            entity?.toIndenBookingAmbilKuitansiModel()
        }
    }

    override suspend fun insert(model: IndenBookingAmbilKuitansiModel): Result<Nothing?> {
        return roomOperation {
            // Delete first, if exist
            val entity = ambilKuitansiDao.get(model.keyId, model.termin)
            if (entity != null) delete(model.keyId, model.termin)

            // Then insert
            ambilKuitansiDao.insert(model.toIndenBookingAmbilKuitansiEntity())

            null
        }
    }

    override suspend fun delete(keyId: String, termin: String): Result<Nothing?> {
        return roomOperation {
            ambilKuitansiDao.delete(keyId, termin)

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            ambilKuitansiDao.deleteAll()

            null
        }
    }


}