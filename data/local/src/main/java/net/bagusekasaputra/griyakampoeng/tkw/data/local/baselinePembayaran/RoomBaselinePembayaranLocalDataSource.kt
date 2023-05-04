package net.bagusekasaputra.griyakampoeng.tkw.data.local.baselinePembayaran

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel

class RoomBaselinePembayaranLocalDataSource(
    roomDatabase: MyRoomDatabase
): LocalBaselinePembayaranDataSource {

    private val dao = roomDatabase.getBaselinePembayaranDao()

    override suspend fun get(kavling: String): Result<BaselinePembayaranModel?> {
        return try {
            val entity = dao.get(kavling)

            Result.success(mapBaselinePembayaranModel(entity))
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?> {
        return try {
            val entity = mapBaselinePembayaranModel(model)!!
            val resultId = dao.insert(entity)

            Log.d("DEBUG_ME", "LocalBaselinePembayaran: Inserting success ${model.kavling} with id $resultId")

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            dao.deleteAll()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }


    private fun mapBaselinePembayaranModel(model: BaselinePembayaranModel?): BaselinePembayaranRoomEntity? {
        return if (model != null) {
            BaselinePembayaranRoomEntity(
                kavling = model.kavling,
                jumlahUang = model.jumlahUang,
                timeMillis = model.timeMillis,
            )
        } else {
            null
        }
    }

    private fun mapBaselinePembayaranModel(entity: BaselinePembayaranRoomEntity?): BaselinePembayaranModel? {
        return if (entity != null) {
            BaselinePembayaranModel(
                kavling = entity.kavling,
                jumlahUang = entity.jumlahUang,
                timeMillis = entity.timeMillis,
            )
        } else {
            null
        }
    }
}