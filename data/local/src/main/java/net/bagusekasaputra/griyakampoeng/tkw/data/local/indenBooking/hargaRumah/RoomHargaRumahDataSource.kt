package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.hargaRumah

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

class RoomHargaRumahDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalHargaRumahIndenBookingDataSource {

    private val hargaRumahDao = myRoomDatabase.getHargaRumahDao()

    override suspend fun get(keyId: String): Result<HargaRumahModel?> {
        return roomOperation {
            hargaRumahDao.get(keyId)?.toModel()
        }
    }

    /**
     * Automatically delete the data when there is found an entity with the same keyId,
     * since using keyId as Primary Key doesn't work
     */
    override suspend fun insert(keyId: String, model: HargaRumahModel): Result<Nothing?> {
        return roomOperation {
            // Delete if it already exist
            val entity = hargaRumahDao.get(model.keyId)
            if (entity != null) {
                hargaRumahDao.delete(keyId)
            }

            // Then insert new
            hargaRumahDao.insert(model.toEntity())

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            hargaRumahDao.deleteAll()

            null
        }
    }
}