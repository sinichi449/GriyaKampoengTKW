package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.StandardAmbilKuitansiModel

interface LocalStandardAmbilKuitansiDataSource {

    suspend fun get(kavling: String, termin: String): Result<StandardAmbilKuitansiModel?>

    suspend fun insert(model: StandardAmbilKuitansiModel): Result<Nothing?>

    suspend fun update(model: StandardAmbilKuitansiModel): Result<Nothing?>

    suspend fun delete(kavling: String, termin: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}