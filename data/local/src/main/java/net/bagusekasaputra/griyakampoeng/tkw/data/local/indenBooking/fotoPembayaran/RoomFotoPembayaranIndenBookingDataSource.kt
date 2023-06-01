package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.fotoPembayaran

import android.net.Uri
import androidx.core.net.toFile
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
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

            entity?.toModel()
        }
    }

    override suspend fun insert(model: FotoPembayaranIndenBookingModel): Result<Nothing?> {
        return roomOperation {
            // Delete first, if already available
            val entityExist = fotoPembayaranDao.get(model.keyId, model.termin) != null
            if (entityExist) delete(model).getOrThrow()

            // Then insert
            fotoPembayaranDao.insert(model.toEntity())

            null
        }
    }

    override suspend fun delete(model: FotoPembayaranIndenBookingModel): Result<Nothing?> {
        return roomOperation {
            fotoPembayaranDao.delete(model.keyId, model.termin)

            // Also, delete cached file too
            val fotoPembayaranFile = Uri.parse(model.uriStr).toFile()
            fotoPembayaranFile.delete()

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