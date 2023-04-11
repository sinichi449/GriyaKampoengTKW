package net.bagusekasaputra.griyakampoengtkw.data.backup.dataDiri

import android.content.SharedPreferences
import net.bagusekasaputra.griyakampoengtkw.data.backup.JSON_DATA_DIRI
import net.bagusekasaputra.griyakampoengtkw.data.backup.getGsonJsonString
import net.bagusekasaputra.griyakampoengtkw.data.backup.writeFile
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import java.io.File

class BackupDataDiriDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupDataDiriDataSource {

    override suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createBackup(
        backupPath: String,
        listDataDiri: Map<String, DataDiriModel?>
    ): Result<Nothing?> {
        return try {
            val listBackup = mutableListOf<BackupDataDiriModel>().apply {
                listDataDiri.keys.forEach { kavling ->
                    this.add(
                        BackupDataDiriModel(
                            kavling = kavling,
                            dataDiri = mapDataDiriModel(listDataDiri[kavling]),
                        )
                    )
                }
            }.toList()
            val file = File("$backupPath/$JSON_DATA_DIRI")
            val json = getGsonJsonString(listBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapDataDiriModel(model: DataDiriModel?): BackupDataDiriModel.DataDiri? {
        return model?.let {
            BackupDataDiriModel.DataDiri(
                alamatIndo = it.alamatIndo,
                alamatKerja = it.alamatKerja,
                jenisIdentitas = it.jenisIdentitas,
                nama = it.nama,
                negaraBekerja = it.negaraBekerja,
                noHp = it.noHp,
                noIdentitas = it.noIdentitas,
            )
        }
    }
}