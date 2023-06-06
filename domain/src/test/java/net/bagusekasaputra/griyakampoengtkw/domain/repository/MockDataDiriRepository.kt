package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri

class MockDataDiriRepository: DataDiriRepository {

    private val mapDataDiri = mutableMapOf(
        "A11" to DataDiri("Laela Nurkumalasari", jenisIdentitas = "KTP", noIdentitas = "", negaraBekerja = "", alamatKerja = "", alamatIndo = "", noHp = "")
    )

    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>> {
        return flow {
            val result = mutableMapOf<String, DataDiri?>()
            listKavling.forEach {
                result[it] = mapDataDiri[it]
            }

            emit(Result.success(result))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>> {
        TODO("Not yet implemented")
    }

    override fun fromBackupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, DataDiri?>?>> {
        TODO("Not yet implemented")
    }

    override fun getDataDiri(kavlingKode: String, dataMode: DataMode): Flow<Result<DataDiri?>> {
        TODO("Not yet implemented")
    }

    override fun getFromRemoteBackup(
        backupName: String,
        kavlingKode: String
    ): Flow<Result<DataDiri?>> {
        TODO("Not yet implemented")
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshCache(kavlings: List<String>): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getFromIndenBooking(keyId: String): Result<DataDiri?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFromIndenBooking(dataDiri: DataDiri): Result<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFromIndenBooking(
        keyId: String,
        newDataDiri: DataDiri
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}