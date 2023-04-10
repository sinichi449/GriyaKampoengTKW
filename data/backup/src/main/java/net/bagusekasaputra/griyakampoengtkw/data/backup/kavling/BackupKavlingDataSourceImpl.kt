package net.bagusekasaputra.griyakampoengtkw.data.backup.kavling

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_KAVLINGS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import java.io.File

class BackupKavlingDataSourceImpl(
    sharedPreferences: SharedPreferences
): BackupKavlingDataSource {

    private val file = "${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_KAVLINGS".let { filePath ->
        File(filePath)
    }

    override suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?> {
        try {
            val backupModels = readJson<Array<BackupKavlingModel>>(file).filter {
                it.blok == blockKode
            }
            backupModels.forEach {
                it.listKavling.forEach { kavling ->
                    Log.d("DEBUG_ME", "Kavling ${kavling.kode} deserialized")
                }
            }

            return if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapListResult(
                    originResult = Result.success(backupModels[0].listKavling),
                    targetMapper = {
                        mapKavlingModel(it)
                    }
                )
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listKavling: HashMap<String, List<KavlingModel>>
    ): Result<Nothing?> {
        return try {
            val listBackupKavling = mutableListOf<BackupKavlingModel>()
            listKavling.keys.forEach { blok ->
                listBackupKavling.add(
                    BackupKavlingModel(
                        blok = blok,
                        listKavling = listKavling.get(blok)?.map {
                            mapKavlingModel(it)
                        } ?: emptyList()
                    )
                )
            }

            val arrKavlingBackup = listBackupKavling.toTypedArray()
            val json = Gson().toJson(arrKavlingBackup)
            val file = File("$backupPath/$JSON_KAVLINGS")

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapKavlingModel(kavling: BackupKavlingModel.Kavling): KavlingModel {
        return kavling.let {
            KavlingModel(
                kode = it.kode,
                warna = it.warna,
                active = it.active,
                ukuran = it.ukuran,
                type = it.type,
            )
        }
    }

    private fun mapKavlingModel(kavlingModel: KavlingModel): BackupKavlingModel.Kavling {
        return kavlingModel.let {
            BackupKavlingModel.Kavling(
                active = it.active,
                kode = it.kode,
                type = it.type,
                ukuran = it.ukuran,
                warna = it.warna,
            )
        }
    }
}