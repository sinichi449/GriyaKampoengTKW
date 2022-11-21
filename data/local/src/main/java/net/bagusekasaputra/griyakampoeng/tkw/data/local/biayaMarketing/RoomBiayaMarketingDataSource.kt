package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

class RoomBiayaMarketingDataSource(
    roomDatabase: MyRoomDatabase
): LocalBiayaMarketingDataSource {

    private val biayaMarketingDao = roomDatabase.getBiayaMarketingDao()

    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?> {
        return RoomRequestHelper.doGetOperation {
            biayaMarketingDao.getAll(kavlingKode)?.map {
                BiayaMarketingModel(
                    timeMillis = it.timeMillis,
                    kavlingKode = it.kavlingKode,
                    jenisBiaya = it.jenisBiaya,
                    harga = it.harga,
                )
            }
        }
    }

    private fun isExist(biayaMarketingModel: BiayaMarketingModel): Boolean {
        return (biayaMarketingDao.getBiayaMarketing(
            kavlingKode = biayaMarketingModel.kavlingKode,
            jenisBiaya = biayaMarketingModel.jenisBiaya,
            harga = biayaMarketingModel.harga,
        ) != null)
    }

    override suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            val dataExist = isExist(biayaMarketingModel)
            if (dataExist)
                biayaMarketingDao.updateBiayaMarketing(
                    kavlingKode = biayaMarketingModel.kavlingKode,
                    newJenisBiaya = biayaMarketingModel.jenisBiaya,
                    newHarga = biayaMarketingModel.harga,
                )
            else
                biayaMarketingDao.insertBiayaMarketing(
                    biayaMarketingModel.let {
                        BiayaMarketingRoomEntity(
                            timeMillis = it.timeMillis,
                            kavlingKode = it.kavlingKode,
                            jenisBiaya = it.jenisBiaya,
                            harga = it.harga,
                        )
                    }
                )
        }
    }

    override suspend fun update(
        kavlingKode: String,
        oldBiayaMarketingModel: BiayaMarketingModel,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.updateBiayaMarketing(
                kavlingKode = newBiayaMarketingModel.kavlingKode,
                newJenisBiaya = newBiayaMarketingModel.jenisBiaya,
                newHarga = newBiayaMarketingModel.harga,
            )
        }
    }

    override suspend fun deleteSingle(kavlingKode: String, timeMillis: Long): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteSingle(kavlingKode, timeMillis)
        }
    }

    override suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteAll(kavlingKode)
        }
    }
}