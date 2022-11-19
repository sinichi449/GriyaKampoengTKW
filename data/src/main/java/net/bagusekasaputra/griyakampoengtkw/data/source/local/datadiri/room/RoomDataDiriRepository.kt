package net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri.room

import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri.LocalDataDiriRepository

class RoomDataDiriRepository(
    roomDatabase: MyRoomDatabase
): LocalDataDiriRepository {

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
        return try {
            dataDiriRoomDao.insert(mapDataDiri(kavlingKode, dataDiriModel))

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun mapDataDiri(dataDiriRoomEntity: DataDiriRoomEntity): DataDiriModel {
        return dataDiriRoomEntity.let {
            DataDiriModel(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas ?: "",
                negaraBekerja = it.negaraBekerja ?: "",
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