package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import android.net.Uri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import java.io.File


class RoomIndenBookingDataSource(
    myRoomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalIndenBookingDataSource {

    private val dataDiriDao = myRoomDatabase.getDataDiriIndenBookingDao()
    private val fotoIdentitasDao = myRoomDatabase.getFotoIdentitasIndenBookingDao()

    override suspend fun getAllKeyIds(): Result<List<String>?> {
        TODO("Not yet implemented")
    }

    override suspend fun getDataDiri(keyId: String): Result<DataDiriModel?> {
        return roomOperation {
            val entity = dataDiriDao.getByKeyId(keyId)

            entity?.toModel()
        }
    }

    override suspend fun getAllPembayaran(keyId: String): Result<List<PembayaranModel>?> {
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

    override suspend fun insertDataDiri(
        keyId: String,
        dataDiriModel: DataDiriModel
    ): Result<Nothing?> {
        return roomOperation {
            val entity = dataDiriModel.toEntity(keyId)

            dataDiriDao.insert(entity)

            null
        }
    }

    override suspend fun insertFotoIdentitas(keyId: String, uri: Uri): Result<Nothing?> {
        return roomOperation {
            val entity = FotoIdentitasIndenBookingEntity(keyId, uri.toString())

            fotoIdentitasDao.insert(entity)

            null
        }
    }

    override suspend fun invalidate(keyId: String): Result<Nothing?> {
        return roomOperation {
            dataDiriDao.delete(keyId)
            fotoIdentitasDao.delete(keyId)

            val fileFoto = File(externalFileDir, "inden_booking_images/data_diri_images/" +
                    "${keyId}.png")
            fileFoto.delete()

            null
        }
    }
}