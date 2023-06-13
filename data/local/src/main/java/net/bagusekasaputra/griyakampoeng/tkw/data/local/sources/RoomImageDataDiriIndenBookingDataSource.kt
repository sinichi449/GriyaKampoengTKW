package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.net.Uri
import androidx.core.net.toFile
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toFotoIdentitasIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toImageDataDiriIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel
import java.io.File

class RoomImageDataDiriIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalImageDataDiriIndenBookingDataSource {

    private val imageDataDiriDao = myRoomDatabase.getFotoIdentitasIndenBookingDao()

    override suspend fun get(keyId: String): Result<ImageDataDiriIndenBookingModel?> {
        return roomOperation {
            val entity = imageDataDiriDao.getByKeyId(keyId)

            entity?.toImageDataDiriIndenBookingModel()
        }
    }

    override suspend fun insert(model: ImageDataDiriIndenBookingModel): Result<Nothing?> {
        return roomOperation {
            // Delete first, if exists
            val entityExists = imageDataDiriDao.getByKeyId(model.keyId) != null
            if (entityExists) delete(model)

            // Then insert
            imageDataDiriDao.insert(model.toFotoIdentitasIndenBookingEntity())

            null
        }
    }

    override suspend fun delete(model: ImageDataDiriIndenBookingModel): Result<Nothing?> {
        return roomOperation {
            imageDataDiriDao.delete(model.keyId)

            // Also delete cached file
            val cachedFile = Uri.parse(model.uriStr).toFile()
            cachedFile.delete()

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            imageDataDiriDao.deleteAll()

            // Also delete all cached File
            val folderPath = ImageDataDiriIndenBookingModel.getFolderPath(externalFileDir)
            folderPath.deleteRecursively()

            null
        }
    }
}