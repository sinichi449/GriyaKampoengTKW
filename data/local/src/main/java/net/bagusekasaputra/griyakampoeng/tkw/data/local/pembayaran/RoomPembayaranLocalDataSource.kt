package net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

class RoomPembayaranLocalDataSource(
    roomDatabase: MyRoomDatabase,
): LocalPembayaranDataSource {

    private val pembayaranDao = roomDatabase.getPembayaranDao()


    private fun mapPembayaranModel(pembayaranRoomEntity: PembayaranRoomEntity): PembayaranModel {
        return pembayaranRoomEntity.let {
            val terminDanUrutan = pisahkanTerminDanUrutan(it.termin)

            PembayaranModel(
                termin =  terminDanUrutan.termin,
                urutan = terminDanUrutan.urutan,
                tanggal = it.tanggal,
                jumlahUangDibayar = it.jumlahUangDibayar,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    private fun mapPembayaranModel(pembayaranModel: PembayaranModel, kavlingKode: String): PembayaranRoomEntity {
        return pembayaranModel.let {
            PembayaranRoomEntity(
                kavlingKode = kavlingKode,
                termin = it.getFullTermin(),
                tanggal = it.tanggal,
                jumlahUangDibayar = it.jumlahUangDibayar,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    private data class TerminDanUrutan(
        val termin: String,
        val urutan: Int,
    )

    private fun pisahkanTerminDanUrutan(termin: String): TerminDanUrutan {
        val pisah = termin.split(" ")
        return TerminDanUrutan(
            termin = pisah[0],
            urutan = pisah[1].toInt(),
        )
    }



    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        return RoomRequestHelper.doGetOperation {
            pembayaranDao.getAllPembayaran(kavlingKode)?.map {
                mapPembayaranModel(it)
            }
        }
    }

    override suspend fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            // Check if data already exist
            val isExist = (pembayaranDao
                .getSinglePembayaran(kavlingKode, pembayaranModel.getFullTermin())
            ) != null

            if (isExist)
                pembayaranDao.updatePembayaran(
                    kavlingKode = kavlingKode,
                    termin = pembayaranModel.getFullTermin(),
                    newTermin = pembayaranModel.getFullTermin(),
                    tanggal = pembayaranModel.tanggal,
                    jumlahUangDibayar = pembayaranModel.jumlahUangDibayar,
                    keterangan = pembayaranModel.keterangan,
                    timeMillis = pembayaranModel.timeMillis,
                )
            else
                pembayaranDao.insertPembayaran(mapPembayaranModel(pembayaranModel, kavlingKode))
        }
    }

    override suspend fun updatePembayaranModel(
        kavlingKode: String,
        oldPembayaranModel: PembayaranModel,
        newPembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            pembayaranDao.updatePembayaran(
                kavlingKode = kavlingKode,
                termin = oldPembayaranModel.getFullTermin(),
                newTermin = newPembayaranModel.getFullTermin(),
                tanggal = newPembayaranModel.tanggal,
                jumlahUangDibayar = newPembayaranModel.jumlahUangDibayar,
                keterangan = newPembayaranModel.keterangan,
                timeMillis = newPembayaranModel.timeMillis,
            )
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
}