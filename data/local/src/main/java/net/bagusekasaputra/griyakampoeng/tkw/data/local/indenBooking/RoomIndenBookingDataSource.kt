package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel

class RoomIndenBookingDataSource(
    roomDatabase: MyRoomDatabase
): LocalIndenBookingDataSource {

    private val dao = roomDatabase.getIndenBookingDao()

    override suspend fun getAll(): Result<List<IndenBookingModel>?> {
        return RoomRequestHelper.doGetOperation {
            dao.getAll()?.map {
                it.toModel()
            }
        }
    }

    override suspend fun insert(model: IndenBookingModel): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            dao.insert(model.toEntity())
        }
    }

    override suspend fun insertAll(listModel: List<IndenBookingModel>): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            listModel.forEach {
                dao.insert(it.toEntity())
            }
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            dao.deleteAll()
        }
    }


    private fun IndenBookingRoomEntity.toModel(): IndenBookingModel {
        return this.let {
            IndenBookingModel(
                timeMillis = it.timeMillis,
                namaCostumer = it.namaCostumer,
                tanggalDibayar = it.tanggalDibayar,
                fotoPembayaranPath = fotoPembayaran,
                jumlahUang = it.jumlahUang,
                noHp = it.noHp,
                keterangan = it.keterangan,
            )
        }
    }

    private fun IndenBookingModel.toEntity(): IndenBookingRoomEntity {
        return this.let {
            IndenBookingRoomEntity(
                timeMillis = it.timeMillis,
                namaCostumer = it.namaCostumer,
                tanggalDibayar = it.tanggalDibayar,
                fotoPembayaran = it.fotoPembayaranPath,
                jumlahUang = it.jumlahUang,
                noHp = it.noHp,
                keterangan = it.keterangan,
            )
        }
    }
}