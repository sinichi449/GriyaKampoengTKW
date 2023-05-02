package net.bagusekasaputra.griyakampoengtkw.data.backup.pembayaran

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_PEMBAYARANS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.getGsonJsonString
import net.bagusekasaputra.griyakampoengtkw.data.backup.readJson
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import java.io.File

class BackupPembayaranDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupPembayaranDataSource {

    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        try {
            val file = "${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_PEMBAYARANS".let { filePath ->
                File(filePath)
            }
            val backupModels = readJson<Array<BackupPembayaranModel>>(file).filter {
                it.kavling == kavlingKode
            }
            backupModels.forEach {
                it.listPembayaran.forEach { pembayaran ->
                    Log.d("DEBUG_ME", "Pembayaran: $kavlingKode termin ${pembayaran.termin} ${pembayaran.urutan} deserialized!")
                }
            }

            return if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapListResult(
                    originResult = Result.success(backupModels[0].listPembayaran),
                    targetMapper = {
                        mapPembayaranModel(it)
                    }
                )
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listPembayaran: Map<String, List<PembayaranModel>?>
    ): Result<Nothing?> {
        return try {
            val arrBackup = mutableListOf<BackupPembayaranModel>().apply {
                listPembayaran.keys.forEach { kavling ->
                    this.add(
                        BackupPembayaranModel(
                            kavling = kavling,
                            listPembayaran = listPembayaran[kavling]?.map {
                                mapPembayaranModel(it)
                            } ?: emptyList()
                        )
                    )
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_PEMBAYARANS")
            val json = getGsonJsonString(arrBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapPembayaranModel(model: PembayaranModel): BackupPembayaranModel.Pembayaran {
        return model.let {
            BackupPembayaranModel.Pembayaran(
                jumlahUangDibayar = it.jumlahUangDibayar,
                keterangan = it.keterangan,
                tanggal = it.tanggal,
                termin = it.termin,
                timeMillis = it.timeMillis,
                urutan = it.urutan,
            )
        }
    }

    private fun mapPembayaranModel(model: BackupPembayaranModel.Pembayaran): PembayaranModel {
        return model.let {
            PembayaranModel(
                termin = it.termin,
                urutan = it.urutan,
                tanggal = it.tanggal,
                jumlahUangDibayar = it.jumlahUangDibayar,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }
}