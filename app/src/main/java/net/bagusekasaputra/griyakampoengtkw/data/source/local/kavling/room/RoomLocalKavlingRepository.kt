package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.room

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomLocalKavlingRepository @Inject constructor(
    roomDatabase: MyRoomDatabase,
): LocalKavlingRepository {

    private val kavlingRoomDao = roomDatabase.getKavlingDao()

    override suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?> {
        return try {
            val kavlingRoomEntities = kavlingRoomDao.getKavlingsByBlockKode(blockKode)
            val mappedKavling = kavlingRoomEntities?.map { mapKavlingRoomEntity(it) }

            Log.d(LOG_TAG, "Getting kavling from local success")

            Result.success(mappedKavling)
        } catch (e: Exception) {
            e.printStackTrace()

            Log.d(LOG_TAG, "Getting kavling from local failed: ${e.message}")

            Result.failure(e)
        }
    }

    override suspend fun addKavling(
        blockKode: String,
        kavlingModel: KavlingModel,
    ): Result<Nothing?> {
        return try {
            val id = kavlingRoomDao.insert(mapKavlingRoomEntity(blockKode, kavlingModel))

            Log.d(LOG_TAG, "Added kavling ${kavlingModel.kode} to Room Database with id $id")

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun mapKavlingRoomEntity(kavlingRoomEntity: KavlingRoomEntity): KavlingModel {
        return kavlingRoomEntity.let {
            KavlingModel(
                kode = it.kode,
                warna = it.warna,
                isActive = it.isActive,
                ukuran = it.ukuran,
                type = it.type,
            )
        }
    }

    private fun mapKavlingRoomEntity(blockKode: String, kavlingModel: KavlingModel): KavlingRoomEntity {
        return kavlingModel.let {
            KavlingRoomEntity(
                blockKode = blockKode,
                kode = it.kode,
                warna = it.warna,
                isActive = it.isActive,
                ukuran = it.ukuran,
                type = it.type,
            )
        }
    }


}