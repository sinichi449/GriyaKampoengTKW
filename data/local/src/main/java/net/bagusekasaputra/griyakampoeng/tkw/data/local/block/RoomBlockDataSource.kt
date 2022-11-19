package net.bagusekasaputra.griyakampoeng.tkw.data.local.block

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
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
        return try {
            blockRoomDao.insert(mapBlockRoomEntity(blockModel))

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

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