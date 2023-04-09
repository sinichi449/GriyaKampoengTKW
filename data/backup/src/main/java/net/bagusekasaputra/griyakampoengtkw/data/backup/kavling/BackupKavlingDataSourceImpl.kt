package net.bagusekasaputra.griyakampoengtkw.data.backup.kavling

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_KAVLINGS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
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

}