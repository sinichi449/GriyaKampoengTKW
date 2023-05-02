package net.bagusekasaputra.griyakampoengtkw.data.backup.catatanPembayaran

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_CATATAN_PEMBAYARAN
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.getGsonJsonString
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel
import java.io.File

class BackupCatatanPembayaranDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
): BackupCatatanPembayaranDataSource {

    override suspend fun getCatatanPembayaran(kavlingKode: String): Result<CatatanPembayaranModel?> {
        return try {
            val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/${JSON_CATATAN_PEMBAYARAN}")
            val backupModels = readJson<Array<BackupCatatanPembayaranModel>>(file).filter {
                it.kavling == kavlingKode
            }

            backupModels.forEach {
                Log.d("DEBUG_ME", "Catatan Pembayaran ${it.kavling} deserialized!")
            }

            return if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapSingleResult(
                    originResult = Result.success(backupModels[0]),
                    targetMapper = ::mapCatatanPembayaranModel,
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listCatatanPembayaran: List<CatatanPembayaranModel>
    ): Result<Nothing?> {
        return try {
            val arrBackup = mutableListOf<BackupCatatanPembayaranModel>().apply {
                listCatatanPembayaran.forEach { model ->
                    this.add(mapCatatanPembayaranModel(model))
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_CATATAN_PEMBAYARAN")
            val json = getGsonJsonString(arrBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun mapCatatanPembayaranModel(model: CatatanPembayaranModel): BackupCatatanPembayaranModel {
        return model.let {
            BackupCatatanPembayaranModel(
                kavling = it.kavlingKode,
                content = it.content,
            )
        }
    }

    private fun mapCatatanPembayaranModel(model: BackupCatatanPembayaranModel): CatatanPembayaranModel {
        return model.let {
            CatatanPembayaranModel(
                kavlingKode = it.kavling,
                content = it.content,
            )
        }
    }
}