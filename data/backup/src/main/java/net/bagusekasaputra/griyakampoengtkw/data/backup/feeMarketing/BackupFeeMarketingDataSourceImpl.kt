package net.bagusekasaputra.griyakampoengtkw.data.backup.feeMarketing

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import java.io.File

class BackupFeeMarketingDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupFeeMarketingDataSource {

    override suspend fun getFeeMarketing(kavlingKode: String): Result<FeeMarketingModel?> {
        return try {
            val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_FEE_MARKETING")
            val listFeeMarketingFilteredKavling = readJson<Array<BackupFeeMarketingModel>>(file).filter {
                it.kavling == kavlingKode
            }

            listFeeMarketingFilteredKavling.forEach {
                Log.d("DEBUG_ME", "Fee Marketing ${it.kavling} a/n ${it.namaMarketer} deserialized!")
            }

            if (listFeeMarketingFilteredKavling.isEmpty()) Result.success(null)
            else DataUtil.mapSingleResult(
                originResult = Result.success(listFeeMarketingFilteredKavling[0]),
                targetMapper = ::mapFeeMarketingModel,
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listFeeMarketing: List<FeeMarketingModel>?
    ): Result<Nothing?> {
        return try {
            val arrBackupFeeMarketing = mutableListOf<BackupFeeMarketingModel>().apply {
                listFeeMarketing?.forEach {
                    this.add(mapFeeMarketingModel(it))
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_FEE_MARKETING")
            val json = getGsonJsonString(arrBackupFeeMarketing)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapFeeMarketingModel(model: FeeMarketingModel): BackupFeeMarketingModel {
        return model.let {
            BackupFeeMarketingModel(
                kavling = it.kavlingKode,
                biayaMarketer = it.biayaMarketer,
                namaMarketer = it.namaMarketer,
                timeMillis = it.timeMillis,
            )
        }
    }

    private fun mapFeeMarketingModel(model: BackupFeeMarketingModel): FeeMarketingModel {
        return model.let {
            FeeMarketingModel(
                timeMillis = it.timeMillis,
                kavlingKode = it.kavling,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer,
            )
        }
    }
}