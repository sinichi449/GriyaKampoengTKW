package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing

import android.util.Log
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
                Log.d("DEBUG_ME", "Get $it")
                BiayaMarketingModel(
                    id = it.id,
                    timeMillis = it.timeMillis,
                    kavlingKode = it.kavlingKode,
                    jenisBiaya = it.jenisBiaya,
                    harga = it.harga,
                )
            }
        }
    }

    override suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
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
        id: Long,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.updateById(
                id = id,
                jenisBiaya = newBiayaMarketingModel.jenisBiaya,
                harga = newBiayaMarketingModel.harga,
            )
        }
    }

    override suspend fun deleteSingle(id: Long): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteById(id)
        }
    }

    override suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteAll(kavlingKode)
        }
    }
}