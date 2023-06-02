package net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.indenBooking

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel

class RoomIndenBookingCatatanPembayaranDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalIndenBookingCatatanPembayaranDataSource {

    private val catatanPembayaranDao by lazy {
        myRoomDatabase.getIndenBookingCatatanPembayaranDao()
    }

    override suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaranModel?> {
        return roomOperation {
            val entity = catatanPembayaranDao.get(keyId)

            entity?.toModel()
        }
    }

    override suspend fun insert(model: IndenBookingCatatanPembayaranModel): Result<Nothing?> {
        return roomOperation {
            // Delete first, if exists
            val isExist = catatanPembayaranDao.get(model.keyId) != null
            if (isExist) delete(model.keyId)

            // Then insert
            catatanPembayaranDao.insert(model.toEntity())

            null
        }
    }

    /**
     * Technically this is the same operation as insert(), since I've set auto-deletion whenever
     * the entity already exists.
     */
    override suspend fun update(
        keyId: String,
        newModel: IndenBookingCatatanPembayaranModel
    ): Result<Nothing?> {
        return insert(newModel)
    }

    override suspend fun delete(keyId: String): Result<Nothing?> {
        return roomOperation {
            catatanPembayaranDao.delete(keyId)

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            catatanPembayaranDao.deleteAll()

            null
        }
    }
}