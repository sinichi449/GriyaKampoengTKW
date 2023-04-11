package net.bagusekasaputra.griyakampoengtkw.data.backup.dataDiri

import android.content.SharedPreferences
import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import java.io.File

class BackupDataDiriDataSourceImpl(
    private val sharedPreferences: SharedPreferences
): BackupDataDiriDataSource {

    override suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?> {
        return try {
            val file = File("${sharedPreferences.getString(PREFS_PATH_DATA_LAMA, "")}/$JSON_DATA_DIRI")
            val backupModels = readJson<Array<BackupDataDiriModel>>(file).filter {
                it.kavling == kavlingKode
            }

            backupModels.forEach {
                val dataDiri = it.dataDiri
                Log.d("DEBUG_ME", "Data Diri ${it.kavling} a/n ${dataDiri?.nama} deserialized!")
            }

            if (backupModels.isEmpty()) {
                Result.success(null)
            } else {
                DataUtil.mapSingleResult(
                    originResult = Result.success(backupModels[0].dataDiri),
                    targetMapper = ::mapDataDiriModel,
                )
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun createBackup(
        backupPath: String,
        listDataDiri: Map<String, DataDiriModel?>
    ): Result<Nothing?> {
        return try {
            val arrBackup = mutableListOf<BackupDataDiriModel>().apply {
                listDataDiri.keys.forEach { kavling ->
                    this.add(
                        BackupDataDiriModel(
                            kavling = kavling,
                            dataDiri = mapDataDiriModel(listDataDiri[kavling]),
                        )
                    )
                }
            }.toTypedArray()
            val file = File("$backupPath/$JSON_DATA_DIRI")
            val json = getGsonJsonString(arrBackup)

            writeFile(file, json)

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapDataDiriModel(model: BackupDataDiriModel.DataDiri): DataDiriModel {
        return model.let {
            DataDiriModel(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                negaraBekerja = it.negaraBekerja,
                alamatKerja = it.alamatKerja,
                alamatIndo = it.alamatIndo,
                noHp = it.noHp,
            )
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