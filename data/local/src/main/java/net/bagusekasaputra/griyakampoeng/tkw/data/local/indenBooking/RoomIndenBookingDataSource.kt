package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import android.net.Uri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import java.io.File


class RoomIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalIndenBookingDataSource {

    private val fotoIdentitasDao = myRoomDatabase.getFotoIdentitasIndenBookingDao()

    override suspend fun getAllKeyIds(): Result<List<String>?> {
        TODO("Not yet implemented")
    }

    override suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun getFotoIdentitas(keyId: String): Result<Uri?> {
        return roomOperation {
            val entity = fotoIdentitasDao.getByKeyId(keyId)

            entity?.uriStr?.let { Uri.parse(it) }
        }
    }

    override suspend fun insertFotoIdentitas(keyId: String, uri: Uri): Result<Nothing?> {
        return roomOperation {
            val entity = FotoIdentitasIndenBookingEntity(keyId, uri.toString())

            fotoIdentitasDao.insert(entity)

            null
        }
    }

    override suspend fun invalidateFotoIdentitas(): Result<Nothing?> {
        return roomOperation {
            val dstFile = File(externalFileDir, "inden_booking_images/data_diri_images")
            if (dstFile.exists()) {
                dstFile.deleteRecursively()
            }

            null
        }
    }

}