package net.bagusekasaputra.griyakampoengtkw.data.backup.biayaMarketing

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import java.io.File

class BackupBiayaMarketingDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupBiayaMarketingDataSource {

    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?> {
        return try {
            val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_BIAYA_MARKETINGS")
            val backupModels = readJson<Array<BackupBiayaMarketingModel>>(file).filter {
                it.kavling == kavlingKode
            }

            backupModels.forEach {
                Log.d("DEBUG_ME", "Biaya Marketing ${it.kavling}: ${it.jenisBiaya} deserialized!")
            }

            if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapListResult(
                    originResult = Result.success(backupModels),
                    targetMapper = ::mapBiayaMarketingModel,
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listBiayaMarketing: List<BiayaMarketingModel>
    ): Result<Nothing?> {
        return try {
            val arrBackup = mutableListOf<BackupBiayaMarketingModel>().apply {
                listBiayaMarketing.forEach { biayaMarketingModel ->
                    this.add(mapBiayaMarketingModel(biayaMarketingModel))
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_BIAYA_MARKETINGS")
            val json = getGsonJsonString(arrBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapBiayaMarketingModel(model: BiayaMarketingModel): BackupBiayaMarketingModel {
        return model.let {
            BackupBiayaMarketingModel(
                kavling = it.kavlingKode,
                harga = it.harga,
                jenisBiaya = it.jenisBiaya,
                tanggal = it.tanggal,
            )
        }
    }

    private fun mapBiayaMarketingModel(model: BackupBiayaMarketingModel): BiayaMarketingModel {
        return model.let {
            BiayaMarketingModel(
                kavlingKode = it.kavling,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
            )
        }
    }
}