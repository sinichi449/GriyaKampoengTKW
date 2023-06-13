package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.net.Uri
import androidx.core.net.toFile
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toFotoPembayaranIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toFotoPembayaranIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel
import java.io.File

class RoomFotoPembayaranIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val rootExternalDir: File?,
): LocalFotoPembayaranIndenBookingDataSource {

    private val fotoPembayaranDao = myRoomDatabase.getFotoPembayaranIndenBookingDao()

    override suspend fun get(
        keyId: String,
        termin: String
    ): Result<FotoPembayaranIndenBookingModel?> {
        return roomOperation {
            val entity = fotoPembayaranDao.get(keyId, termin)

            entity?.toFotoPembayaranIndenBookingModel()
        }
    }

    override suspend fun insert(model: FotoPembayaranIndenBookingModel): Result<Nothing?> {
        return roomOperation {
            // Delete first, if already available
            val entityExist = fotoPembayaranDao.get(model.keyId, model.termin) != null
            if (entityExist) delete(model.keyId, model.termin).getOrThrow()

            // Then insert
            fotoPembayaranDao.insert(model.toFotoPembayaranIndenBookingEntity())

            null
        }
    }

    override suspend fun delete(keyId: String, termin: String): Result<Nothing?> {
        return roomOperation {
            val entity = get(keyId, termin).getOrThrow()

            // Delete cached file
            if (entity != null) {
                val fotoPembayaranFile = Uri.parse(entity.uriStr).toFile()
                fotoPembayaranFile.delete()
            }

            // Then delete database listing
            fotoPembayaranDao.delete(keyId, termin)

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return roomOperation {
            fotoPembayaranDao.deleteAll()

            // Also, delete all cached file too
            val folderPath = FotoPembayaranIndenBookingModel.getFolderPath(rootExternalDir)
            folderPath.deleteRecursively()

            null
        }
    }
}