package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toHargaRumahEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toHargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

class RoomHargaRumahDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalHargaRumahIndenBookingDataSource {

    private val hargaRumahDao = myRoomDatabase.getHargaRumahDao()

    override suspend fun get(keyId: String): Result<HargaRumahModel?> {
        return roomOperation {
            hargaRumahDao.get(keyId)?.toHargaRumahModel()
        }
    }

    /**
     * Automatically delete the data when there is found an entity with the same keyId.
     */
    override suspend fun insert(keyId: String, model: HargaRumahModel): Result<Nothing?> {
        return roomOperation {
            // Delete if it already exist
            val entity = hargaRumahDao.get(model.keyId)
            if (entity != null) {
                hargaRumahDao.delete(keyId)
            }

            // Then insert new
            hargaRumahDao.insert(model.toHargaRumahEntity())

            null
        }
    }

    /**
     * Technically, update() method should already implemented inside insert(), since insert()
     * method automatically delete any entities which has the same keyId.
     * So, here it is.
     */
    override suspend fun update(keyId: String, newModel: HargaRumahModel): Result<Nothing?> {
        return insert(keyId, newModel)
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            hargaRumahDao.deleteAll()

            null
        }
    }
}