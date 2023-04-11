package net.bagusekasaputra.griyakampoengtkw.data.backup.hargaKavling

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import java.io.File

class BackupHargaKavlingDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupHargaKavlingDataSource {

    override suspend fun getHargaKavling(kavlingKode: String): Result<HargaKavlingModel?> {
        return try {
            val file = "${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_HARGA_KAVLINGS".let { filePath ->
                File(filePath)
            }
            val backupModels = readJson<Array<BackupHargaKavlingModel>>(file).filter {
                it.kavling == kavlingKode
            }
            backupModels.forEach {
                Log.d("DEBUG_ME", "Harga Kavling ${it.kavling} deserialized!")
            }

            if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapSingleResult(
                    originResult = Result.success(backupModels[0]),
                    targetMapper = ::mapHargaKavlingModel,
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listHargaKavling: List<HargaKavlingModel>?
    ): Result<Nothing?> {
        return try {
            val arrBackup = mutableListOf<BackupHargaKavlingModel>().apply {
                listHargaKavling?.forEach {
                    this.add(mapHargaKavlingModel(it))
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_HARGA_KAVLINGS")
            val json = getGsonJsonString(arrBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun mapHargaKavlingModel(model: BackupHargaKavlingModel): HargaKavlingModel {
        return model.let {
            HargaKavlingModel(
                kavlingKode = it.kavling,
                harga = it.hargaKavling,
                tambahLuasan = it.tambahLuasan,
            )
        }
    }

    private fun mapHargaKavlingModel(model: HargaKavlingModel): BackupHargaKavlingModel {
        return model.let {
            BackupHargaKavlingModel(
                kavling = it.kavlingKode,
                hargaKavling = it.harga,
                tambahLuasan = it.tambahLuasan,
            )
        }
    }
}