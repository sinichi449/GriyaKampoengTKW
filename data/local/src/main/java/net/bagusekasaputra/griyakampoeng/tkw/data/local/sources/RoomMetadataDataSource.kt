package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.MetadataEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

class RoomMetadataDataSource(
    roomDatabase: MyRoomDatabase,
): LocalMetadataDataSource {

    private val metadataDao = roomDatabase.getMetadataDao()

    override fun get(tableName: String): MetadataModel? {
        return try {
            val entity = metadataDao.get(tableName)

            if (entity != null) {
                MetadataModel(
                    tableName = entity.tableName,
                    timestamp = entity.timestamp,
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Log.d("DEBUG_ME", "Error getting metadata from room: ${e.message}")

            null
        }
    }

    override suspend fun insert(model: MetadataModel) {
        try {
            metadataDao.insert(
                MetadataEntity(
                    tableName = model.tableName,
                    timestamp = model.timestamp,
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()

            Log.d("DEBUG_ME", "Error inserting metadata to room: ${e.message}")

            throw e
        }
    }
}