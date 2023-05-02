package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

interface LocalBiayaLainDataSource {

    fun getAll(): Flow<Result<List<BiayaLainModel>?>>

    fun getSingle(jenisBiaya: String): Flow<Result<BiayaLainModel?>>

    suspend fun insertAll(listModel: List<BiayaLainModel>): Result<Nothing?>

    suspend fun insert(model: BiayaLainModel): Result<Nothing?>

    suspend fun update(oldModel: BiayaLainModel, newModel: BiayaLainModel): Result<Nothing?>

    suspend fun delete(model: BiayaLainModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}