package net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
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
        val targetData = kavlingRoomDao.getSingleKavling(blockKode, kavlingModel.kode)
        return RoomRequestHelper.doInsertPreventDuplicateOperation(
            outerData = kavlingModel,
            targetData = targetData,
            equalityPredicate = { m, e ->
                ((m.kode == e.kode)
                        and (m.warna == e.warna)
                        and (m.active == e.isActive)
                        and (m.ukuran == e.ukuran)
                        and (m.type == e.type))
            },
            insertWork = {
                kavlingRoomDao.insert(mapKavlingRoomEntity(blockKode, it))
            }
        )
    }

    override suspend fun addAll(kavlingModels: List<KavlingModel>): Result<Nothing?> {
        return try {
            kavlingModels.forEach { model ->
                val blokKode = KavlingModel.getBlockKode(model.kode)
                val kavlingEntity = mapKavlingRoomEntity(blokKode, model)

                kavlingRoomDao.insert(kavlingEntity)
            }

            Result.success(null)
        } catch (e: Exception) {
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

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            kavlingRoomDao.deleteAll()

            Result.success(null)
        } catch (e: Exception) {
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