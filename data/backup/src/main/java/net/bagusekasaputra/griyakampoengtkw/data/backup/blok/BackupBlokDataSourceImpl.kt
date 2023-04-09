package net.bagusekasaputra.griyakampoengtkw.data.backup.blok

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import net.bagusekasaputra.griyakampoengtkw.data.backup.BLOKS_JSON
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.readFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import java.io.File
import java.io.FileNotFoundException

class BackupBlokDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
): BackupBlokDataSource {

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        val dataLamaPath = "${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$BLOKS_JSON"
        val file = File(dataLamaPath)

        if (file.exists().not()) {
            return Result.failure(FileNotFoundException("$dataLamaPath is nowhere to be found"))
        }

        val jsonString = readFile(file)
        val bloksArray = Gson().fromJson(jsonString, Array<BackupBlokModel>::class.java)

        Log.d("DEBUG_ME", jsonString)

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