package net.bagusekasaputra.griyakampoengtkw.data.backup.blok

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_BLOKS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import java.io.File

class BackupBlokDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
): BackupBlokDataSource {

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        val dataLamaPath = "${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_BLOKS"
        val file = File(dataLamaPath)

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

    private fun mapBlok(blokModel: BackupBlokModel): BlockModel {
        return BlockModel(
            kode = blokModel.kode,
            warna = blokModel.warna,
        )
    }
}