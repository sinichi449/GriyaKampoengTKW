package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.pembayaran

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.LocalPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

class RoomPembayaranIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalPembayaranIndenBookingDataSource {

    private val pembayaranDao  = myRoomDatabase.getPembayaranIndenBookingDao()

    override suspend fun getAll(keyId: String): Result<List<PembayaranModel>?> {
        return RoomRequestHelper.roomOperation {
            val entityList = pembayaranDao.getAllByKeyId(keyId)

            entityList?.map { it.toModel() }
        }
    }

    override suspend fun insertAll(keyId: String, models: List<PembayaranModel>): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            val entityList = models.map { it.toEntity(keyId) }

            pembayaranDao.insertAll(entityList).forEach {
                Log.d("INTERNAL_INDEN_BOOKING", "Inserting with id $it !!")
            }

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            pembayaranDao.deleteAll()

            null
        }
    }
}