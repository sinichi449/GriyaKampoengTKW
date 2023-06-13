package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BaselinePembayaranRoomEntity
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

            dao.insert(entity)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun addAll(models: List<BaselinePembayaranModel>): Result<Nothing?> {
        return try {
            val baselinePembayaranEntities = models.map {
                mapBaselinePembayaranModel(it)!!
            }
            val resultId = dao.insertAll(baselinePembayaranEntities)

            resultId.forEach {
                Log.d("INIT_CACHE", "Inserting Baseline Pembayaran success with id \"$it\"")
            }

            Result.success(null)
        } catch (e: Exception) {
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
                opsiBulan = model.opsiBulan,
                jumlahUang = model.jumlahUang,
                tanggalPembayaranMaks = model.tanggalPembayaranMaks,
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
                opsiBulan = entity.opsiBulan,
                jumlahUang = entity.jumlahUang,
                tanggalPembayaranMaks = entity.tanggalPembayaranMaks,
                timeMillis = entity.timeMillis,
            )
        } else {
            null
        }
    }
}