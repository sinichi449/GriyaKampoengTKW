package net.bagusekasaputra.griyakampoengtkw.data.backup.blok

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_BLOKS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import java.io.File

class BackupBlokDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
): BackupBlokDataSource {

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_BLOKS")

        val bloksArray = readJson<Array<BackupBlokModel>>(file)

        val listMappedBlok = bloksArray.map {
            mapBlok(it)
        }
        listMappedBlok.forEach {
            Log.d("DEBUG_ME", "Blok deserialized kode: ${it.kode} warna: ${it.warna}")
        }

        return if (listMappedBlok.isEmpty()) {
            Result.success(null)
        } else {
            Result.success(listMappedBlok)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listBlok: List<BlockModel>
    ): Result<Nothing?> {
        return try {
            val arrBlokBackup = listBlok.map {
                mapBlok(it)
            }.toTypedArray()

            val json = Gson().toJson(arrBlokBackup)
            val file = File("$backupPath/$JSON_BLOKS")
            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapBlok(blokModel: BackupBlokModel): BlockModel {
        return BlockModel(
            kode = blokModel.kode,
            warna = blokModel.warna,
        )
    }

    private fun mapBlok(model: BlockModel): BackupBlokModel {
        return model.let {
            BackupBlokModel(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }
}