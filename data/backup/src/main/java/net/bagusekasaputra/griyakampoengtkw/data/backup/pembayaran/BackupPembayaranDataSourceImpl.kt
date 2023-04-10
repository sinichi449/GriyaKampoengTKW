package net.bagusekasaputra.griyakampoengtkw.data.backup.pembayaran

import android.content.SharedPreferences
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_PEMBAYARANS
import net.bagusekasaputra.griyakampoengtkw.data.backup.PREFS_PATH_DATA_LAMA
import net.bagusekasaputra.griyakampoengtkw.data.backup.getGsonJsonString
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import java.io.File

class BackupPembayaranDataSourceImpl(
    sharedPreferences: SharedPreferences
): BackupPembayaranDataSource {

    private val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_PEMBAYARANS")

    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listPembayaran: Map<String, List<PembayaranModel>?>
    ): Result<Nothing?> {
        return try {
            val listPembayaranBackup = mutableListOf<BackupPembayaranModel>().apply {
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
            }

            val json = getGsonJsonString(listPembayaranBackup)

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
}