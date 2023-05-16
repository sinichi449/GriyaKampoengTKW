package net.bagusekasaputra.griyakampoeng.tkw.data.local.block

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel

class RoomBlockDataSource(
    roomDatabase: MyRoomDatabase,
): LocalBlockDataSource {

    private val blockRoomDao = roomDatabase.getBlockDao()

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        return try {
            val blockModel = blockRoomDao.getAllBlocks()?.map { mapBlockRoomEntity(it) }

            Result.success(blockModel)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun addBlock(blockModel: BlockModel): Result<Nothing?> {
        val targetData = blockRoomDao.getSingleBlock(blockModel.kode)
        return RoomRequestHelper.doInsertPreventDuplicateOperation(
            outerData = blockModel,
            targetData = targetData,
            equalityPredicate = { m, e ->
                ((m.kode == e.kode)
                        and (m.warna == e.warna))
            },
            insertWork = {
                blockRoomDao.insert(mapBlockRoomEntity(it))
            }
        )
    }

    override suspend fun addAll(blockModels: List<BlockModel>): Result<Nothing?> {
        return try {
            blockModels.forEach { model ->
                val blockEntity = mapBlockRoomEntity(model)

                blockRoomDao.insert(blockEntity)
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            blockRoomDao.deleteAll()

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun mapBlockRoomEntity(blockRoomEntity: BlockRoomEntity): BlockModel {
        return blockRoomEntity.let {
            BlockModel(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }

    private fun mapBlockRoomEntity(blockModel: BlockModel): BlockRoomEntity {
        return blockModel.let {
            BlockRoomEntity(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }
}