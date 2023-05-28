package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.AmbilKuitansiModel

interface LocalAmbilKuitansiDataSource {

    suspend fun get(kavling: String, termin: String): Result<AmbilKuitansiModel?>

    suspend fun insert(model: AmbilKuitansiModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}