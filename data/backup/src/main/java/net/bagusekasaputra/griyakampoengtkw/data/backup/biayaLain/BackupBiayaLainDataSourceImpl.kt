package net.bagusekasaputra.griyakampoengtkw.data.backup.biayaLain

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_BIAYA_LAINS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.getGsonJsonString
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel
import java.io.File

class BackupBiayaLainDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupBiayaLainDataSource {

    override suspend fun getAllBiayaLain(): Result<List<BiayaLainModel>?> {
        return try {
            val fileJsonBiayaLain = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_BIAYA_LAINS")
            val arrBackupBiayaLain = readJson<Array<BackupBiayaLainModel>>(fileJsonBiayaLain)

            arrBackupBiayaLain.forEach {
                Log.d("DEBUG_ME", "Biaya Lain: \"${it.jenisBiaya}\" deserialized!")
            }

            if (arrBackupBiayaLain.isEmpty()) Result.success(null)
            else DataUtil.mapListResult(
                originResult = Result.success(arrBackupBiayaLain.toList()),
                targetMapper = ::mapBiayaLainModel,
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listBiayaLain: List<BiayaLainModel>?
    ): Result<Nothing?> {
        return try {
            val arrBackupBiayaLain = mutableListOf<BackupBiayaLainModel>().apply {
                listBiayaLain?.forEach {
                    this.add(mapBiayaLainModel(it))
                }
            }.toTypedArray()
            val fileBiayaLainDstBackup = File("$backupPath/$JSON_BIAYA_LAINS")
            val json = getGsonJsonString(arrBackupBiayaLain)

            writeFile(fileBiayaLainDstBackup, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapBiayaLainModel(model: BackupBiayaLainModel): BiayaLainModel {
        return model.let {
            BiayaLainModel(
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
                tanggal = it.tanggal,
            )
        }
    }

    private fun mapBiayaLainModel(model: BiayaLainModel): BackupBiayaLainModel {
        return model.let {
            BackupBiayaLainModel(
                harga = it.harga,
                jenisBiaya = it.jenisBiaya,
                tanggal = it.tanggal,
            )
        }
    }
}