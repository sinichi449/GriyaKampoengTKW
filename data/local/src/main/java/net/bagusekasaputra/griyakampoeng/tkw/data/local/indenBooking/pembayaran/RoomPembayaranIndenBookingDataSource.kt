package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.pembayaran

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.LocalPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

class RoomPembayaranIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalPembayaranIndenBookingDataSource {

    private val pembayaranDao  = myRoomDatabase.getPembayaranIndenBookingDao()

    override suspend fun getAll(keyId: String): Result<List<PembayaranModel>?> {
        return roomOperation {
            val entityList = pembayaranDao.getAllByKeyId(keyId)

            entityList?.map { it.toModel() }
        }
    }

    override suspend fun insert(keyId: String, model: PembayaranModel): Result<Nothing?> {
        return roomOperation {
            pembayaranDao.insert(model.toEntity(keyId))

            null
        }
    }

    override suspend fun insertAll(keyId: String, models: List<PembayaranModel>): Result<Nothing?> {
        return roomOperation {
            val entityList = models.map { it.toEntity(keyId) }

            pembayaranDao.insertAll(entityList).forEach {
                Log.d("INTERNAL_INDEN_BOOKING", "Inserting with id $it !!")
            }

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            pembayaranDao.deleteAll()

            null
        }
    }
}