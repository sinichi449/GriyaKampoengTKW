package net.bagusekasaputra.griyakampoengtkw.data.source.local.block.room

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.LocalBlockDataSource

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