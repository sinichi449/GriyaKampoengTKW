package net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

class RoomDataDiriDataSource(
    private val roomDatabase: MyRoomDatabase,
    private val dataDiriIndenBookingDataSource: RoomDataDiriIndenBookingDataSource,
): LocalDataDiriDataSource {

    private val dataDiriRoomDao = roomDatabase.getDataDiriDao()

    override suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?> {
        return try {
            val dataDiri = dataDiriRoomDao.getByKavlingKode(kavlingKode)

            if (dataDiri != null)
                Result.success(mapDataDiri(dataDiri))
            else
                Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun addDataDiri(
        kavlingKode: String,
        dataDiriModel: DataDiriModel,
    ): Result<Nothing?> {
        val targetData = dataDiriRoomDao.getByKavlingKode(kavlingKode)
        return RoomRequestHelper.doInsertPreventDuplicateOperation(
            outerData = dataDiriModel,
            targetData = targetData,
            equalityPredicate = { m, e ->
                ((m.nama == e.nama)
                        and (m.jenisIdentitas == e.jenisIdentitas)
                        and (m.noIdentitas == e.noIdentitas)
                        and (m.negaraBekerja == e.negaraBekerja)
                        and (m.alamatKerja == e.alamatKerja)
                        and (m.alamatIndo == e.alamatIndo)
                        and (m.noHp == e.noHp))
            },
            insertWork = {
                dataDiriRoomDao.insert(mapDataDiri(kavlingKode, it))
            }
        )
    }

    override suspend fun deleteDataDiri(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            dataDiriRoomDao.deleteByKavlingKode(kavlingKode)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            dataDiriRoomDao.deleteAll()

            roomDatabase

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



            Result.failure(e)
        }
    }


    /**
     * Inden Booking related
     */
    override suspend fun getFromIndenBooking(keyId: String): Result<DataDiriModel?> {
        return dataDiriIndenBookingDataSource.get(keyId)
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        model: DataDiriModel
    ): Result<Nothing?> {
        return dataDiriIndenBookingDataSource.insert(keyId, model)
    }

    override suspend fun deleteAllFromIndenBooking(): Result<Nothing?> {
        return dataDiriIndenBookingDataSource.deleteAll()
    }

    override suspend fun updateFromIndenBooking(
        keyId: String,
        newModel: DataDiriModel
    ): Result<Nothing?> {
        return dataDiriIndenBookingDataSource.update(keyId, newModel)
    }


    private fun mapDataDiri(dataDiriRoomEntity: DataDiriRoomEntity): DataDiriModel {
        return dataDiriRoomEntity.let {
            DataDiriModel(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas ?: "",
                negaraBekerja = it.negaraBekerja,
                alamatKerja = it.alamatKerja ?: "",
                alamatIndo = it.alamatIndo ?: "",
                noHp = it.noHp ?: "",
            )
        }
    }

    private fun mapDataDiri(kavlingKode: String, dataDiriModel: DataDiriModel): DataDiriRoomEntity {
        return dataDiriModel.let {
            DataDiriRoomEntity(
                kavlingKode = kavlingKode,
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                negaraBekerja = it.negaraBekerja,
                alamatKerja = it.alamatKerja,
                alamatIndo = it.alamatIndo,
                noHp = it.noHp,
            )
        }
    }

}

/**
 * This interface will prevent RoomDataDiriDataSource depending on the Inden Booking
 * counterpart, since it is very unstable, and instead inverting that relation.
 *
 * (Dependency Inversion?)
 */
interface RoomDataDiriIndenBookingDataSource {
    suspend fun get(keyId: String): Result<DataDiriModel?>

    suspend fun insert(keyId: String, model: DataDiriModel): Result<Nothing?>

    suspend fun update(keyId: String, newModel: DataDiriModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}