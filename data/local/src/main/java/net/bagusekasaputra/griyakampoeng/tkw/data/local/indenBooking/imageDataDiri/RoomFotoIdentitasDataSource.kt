package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.imageDataDiri

import android.net.Uri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.LocalFotoIdentitasDataSource
import java.io.File

class RoomFotoIdentitasDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalFotoIdentitasDataSource {

    private val fotoIdentitasDao = myRoomDatabase.getFotoIdentitasIndenBookingDao()

    override suspend fun get(keyId: String): Result<Uri?> {
        return RoomRequestHelper.roomOperation {
            val entity = fotoIdentitasDao.getByKeyId(keyId)

            entity?.uriStr?.let { Uri.parse(it) }
        }
    }

    override suspend fun insert(keyId: String, uri: Uri): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            val entity = FotoIdentitasIndenBookingEntity(keyId, uri.toString())

            fotoIdentitasDao.insert(entity)

            null
        }
    }

    override suspend fun update(keyId: String, newUri: Uri): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            // delete first
            fotoIdentitasDao.delete(keyId)

            // then insert new
            val entity = FotoIdentitasIndenBookingEntity(keyId, newUri.toString())
            fotoIdentitasDao.insert(entity)

            null
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return RoomRequestHelper.roomOperation {
            val dstFile = File(externalFileDir, "inden_booking_images/data_diri_images")
            if (dstFile.exists()) {
                dstFile.deleteRecursively()
            }

            fotoIdentitasDao.deleteAll()

            null
        }
    }
}