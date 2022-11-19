package net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

class RoomKavlingDataSource(
    roomDatabase: MyRoomDatabase,
): LocalKavlingDataSource {

    private val kavlingRoomDao = roomDatabase.getKavlingDao()

    override suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?> {
        return try {
            val kavlingRoomEntities = kavlingRoomDao.getKavlingsByBlockKode(blockKode)
            val mappedKavling = kavlingRoomEntities?.map { mapKavlingRoomEntity(it) }

            Result.success(mappedKavling)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun addKavling(
        blockKode: String,
        kavlingModel: KavlingModel,
    ): Result<Nothing?> {
        return try {
            val id = kavlingRoomDao.insert(mapKavlingRoomEntity(blockKode, kavlingModel))

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun deleteKavling(kavlingKode: String): Result<Nothing?> {
        return try {
            kavlingRoomDao.deleteKavling(kavlingKode)

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
                active = it.isActive,
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
                isActive = it.active,
                ukuran = it.ukuran,
                type = it.type,
            )
        }
    }


}