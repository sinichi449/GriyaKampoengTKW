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
    private val pembayaranDao  = myRoomDatabase.getPembayaranIndenBookingDao()
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
        return roomOperation {
            val entityList = pembayaranDao.getAllByKeyId(keyId)

            entityList?.map { it.toModel() }
        }
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

    override suspend fun insertAllPembayaran(
        keyId: String,
        pembayaranList: List<PembayaranModel>
    ): Result<Nothing?> {
        return roomOperation {
            val entityList = pembayaranList.map { it.toEntity(keyId) }

            pembayaranDao.insertAll(entityList)

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

    override suspend fun invalidateDataDiri(): Result<Nothing?> {
        return roomOperation {
            dataDiriDao.deleteAll()

            null
        }
    }

    override suspend fun invalidatePembayaran(): Result<Nothing?> {
        return roomOperation {
            pembayaranDao.deleteAll()

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