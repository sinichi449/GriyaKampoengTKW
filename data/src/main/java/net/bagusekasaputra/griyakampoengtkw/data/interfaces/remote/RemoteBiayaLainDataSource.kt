package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

interface RemoteBiayaLainDataSource {

    fun getAll(): Flow<Result<List<BiayaLainModel>?>>

    suspend fun getSingle(jenisBiaya: String): Result<BiayaLainModel?>

    suspend fun addBiaya(model: BiayaLainModel): Result<Nothing?>

    suspend fun delete(model: BiayaLainModel): Result<Nothing?>

    suspend fun update(oldModel: BiayaLainModel, newModel: BiayaLainModel): Result<Nothing?>
}