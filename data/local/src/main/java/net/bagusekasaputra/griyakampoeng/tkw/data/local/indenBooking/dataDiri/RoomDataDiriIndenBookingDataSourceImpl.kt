package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.dataDiri

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.RoomDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

class RoomDataDiriIndenBookingDataSourceImpl(
    myRoomDatabase: MyRoomDatabase
): RoomDataDiriIndenBookingDataSource {

    private val dataDiriDao = myRoomDatabase.getDataDiriIndenBookingDao()

    override suspend fun get(keyId: String): Result<DataDiriModel?> {
        return RoomRequestHelper.roomOperation {
            val entity = dataDiriDao.getByKeyId(keyId)

            entity?.toModel()
        }
    }

    override suspend fun insert(
        keyId: String,
        model: DataDiriModel
    ): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            val entity = model.toEntity(keyId)

            dataDiriDao.insert(entity)

            null
        }
    }

    override suspend fun update(keyId: String, newModel: DataDiriModel): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            // delete first
            dataDiriDao.delete(keyId)

            // then insert new
            dataDiriDao.insert(newModel.toEntity(keyId))

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            dataDiriDao.deleteAll()

            null
        }
    }
}