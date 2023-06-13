package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.toPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

class RoomPembayaranLocalDataSource(
    roomDatabase: MyRoomDatabase,
    private val pembayaranIndenBookingDataSource: LocalPembayaranIndenBookingDataSource,
): LocalPembayaranDataSource {

    private val pembayaranDao = roomDatabase.getPembayaranDao()

    override suspend fun getByKavlingAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<PembayaranModel?> {
        return roomOperation {
            val entity = pembayaranDao.getSinglePembayaran(kavlingKode, termin)

            entity?.toPembayaranModel()
        }
    }

    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        return RoomRequestHelper.doGetOperation {
            pembayaranDao.getAllPembayaran(kavlingKode)?.map {
                it.toPembayaranModel()
            }
        }
    }

    override suspend fun addPembayaranModel(kavlingKode: String, pembayaranModel: PembayaranModel): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            // Check if data already exist
            val entity = pembayaranDao.getSinglePembayaran(
                kavlingKode, pembayaranModel.getFullTermin()
            )

            if (entity != null) {
                pembayaranDao.updatePembayaran(
                    kavlingKode = kavlingKode,
                    termin = pembayaranModel.getFullTermin(),
                    newTermin = pembayaranModel.getFullTermin(),
                    tanggal = pembayaranModel.tanggal,
                    jumlahUangDibayar = pembayaranModel.jumlahUangDibayar,
                    invoiceDate = pembayaranModel.invoiceDateStr,
                    keterangan = pembayaranModel.keterangan,
                    timeMillis = pembayaranModel.timeMillis,
                )
            } else {
                pembayaranDao.insertPembayaran(pembayaranModel.toEntity(kavlingKode))
            }
        }
    }

    override suspend fun addAllPembayaranModel(
        kavlingKode: String,
        models: List<PembayaranModel>
    ): Result<Nothing?> {
        return roomOperation {
            val entityList = models.map {
                it.toEntity(kavlingKode)
            }
            val resultIds = pembayaranDao.insertAll(entityList)
            resultIds.forEach {
                Log.d("PROGRESS_KAVLING", "Inserting $kavlingKode with id $it !")
            }

            null
        }
    }

    @Deprecated("")
    suspend fun addAllPembayaranModelLegacy(
        kavlingKode: String,
        models: List<PembayaranModel>
    ): Result<Nothing?> {
        return try {
            models.forEach { pembayaranModel ->
                val pembayaranEntity = pembayaranModel.toEntity(kavlingKode)

                val id = pembayaranDao.insertPembayaran(pembayaranEntity)
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun update(
        kavlingKode: String,
        termin: String,
        newModel: PembayaranModel
    ): Result<Nothing?> {
        return roomOperation {
            // Delete first
            pembayaranDao.deleteByKavlingKodeAndTermin(kavlingKode, termin)

            // Then insert
            pembayaranDao.insertPembayaran(newModel.toEntity(kavlingKode))

            null
        }
    }


    override suspend fun deletePembayaranModelByTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            pembayaranDao.deleteByKavlingKodeAndTermin(kavlingKode, termin)
        }
    }

    override suspend fun deleteAllPembayaranModel(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            pembayaranDao.deleteAllInKavling(kavlingKode)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            pembayaranDao.deleteAll()



            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



            Result.failure(e)
        }
    }


    /**
     * Inden Booking related
     */
    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<PembayaranModel>?> {
        return pembayaranIndenBookingDataSource.getAll(keyId)
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        model: PembayaranModel
    ): Result<Nothing?> {
        return pembayaranIndenBookingDataSource.insert(keyId, model)
    }

    override suspend fun insertAllFromIndenBooking(
        keyId: String,
        models: List<PembayaranModel>
    ): Result<Nothing?> {
        return pembayaranIndenBookingDataSource.insertAll(keyId, models)
    }

    override suspend fun deleteAllFromIndenBooking(): Result<Nothing?> {
        return pembayaranIndenBookingDataSource.deleteAll()
    }
}

/**
 * This interface will prevent dependency to Inden Booking counterpart, since it is very unstable,
 * and instead inverting that relation.
 *
 * (Dependency Inversion?)
 */
interface LocalPembayaranIndenBookingDataSource {

    suspend fun getAll(keyId: String): Result<List<PembayaranModel>?>

    suspend fun insert(keyId: String, model: PembayaranModel): Result<Nothing?>

    suspend fun insertAll(keyId: String, models: List<PembayaranModel>): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}