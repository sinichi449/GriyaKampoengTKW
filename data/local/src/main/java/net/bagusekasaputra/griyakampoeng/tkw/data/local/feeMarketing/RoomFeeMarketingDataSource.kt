package net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel

class RoomFeeMarketingDataSource(
    private val roomDatabase: MyRoomDatabase
): LocalFeeMarketingDataSource {

    private val feeMarketingDao = roomDatabase.getFeeMarketingDao()

    override suspend fun getByKavlingKode(kavlingKode: String): Result<FeeMarketingModel?> {
        return RoomRequestHelper.doGetOperation {
            feeMarketingDao.getByKavlingKode(kavlingKode).let {
                if (it != null)
                    FeeMarketingModel(
                        timeMillis = it.timeMillis,
                        kavlingKode = it.kavlingKode,
                        namaMarketer = it.namaMarketer,
                        biayaMarketer = it.biayaMarketer,
                    )
                else
                    null
            }
        }
    }

    private fun isDataExist(feeMarketingModel: FeeMarketingModel): Boolean {
        return (feeMarketingDao.getByKavlingKode(feeMarketingModel.kavlingKode) != null)
    }

    override suspend fun addFeeMarketing(
        kavlingKode: String,
        feeMarketingModel: FeeMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            val dataExist = isDataExist(feeMarketingModel)
            if (dataExist)
                feeMarketingDao.updateFeeMarketing(
                    kavlingKode = kavlingKode,
                    newNamaMarketer = feeMarketingModel.namaMarketer,
                    newBiayaMarketer = feeMarketingModel.biayaMarketer,
                    newTimeMillis = feeMarketingModel.timeMillis ?: 0L,
                )
            else
                feeMarketingDao.insertFeeMarketing(
                    feeMarketingModel.let {
                        FeeMarketingRoomEntity(
                            timeMillis = it.timeMillis,
                            kavlingKode = kavlingKode,
                            namaMarketer = it.namaMarketer,
                            biayaMarketer = it.biayaMarketer,
                        )
                    }
                )
        }
    }

    override suspend fun updateFeeMarketing(
        kavlingKode: String,
        oldFeeMarketingModel: FeeMarketingModel,
        newFeeMarketingModel: FeeMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            feeMarketingDao.updateFeeMarketing(
                kavlingKode = kavlingKode,
                newNamaMarketer = newFeeMarketingModel.namaMarketer,
                newBiayaMarketer = newFeeMarketingModel.biayaMarketer,
                newTimeMillis = newFeeMarketingModel.timeMillis ?: 0L,
            )
        }
    }

    override suspend fun deleteFeeMarketing(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            feeMarketingDao.deleteFeeMarketing(kavlingKode)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            feeMarketingDao.deleteAll()



            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



            Result.failure(e)
        }
    }
}