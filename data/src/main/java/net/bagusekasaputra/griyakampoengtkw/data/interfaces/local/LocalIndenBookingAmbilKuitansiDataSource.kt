package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel

interface LocalIndenBookingAmbilKuitansiDataSource {

    suspend fun get(keyId: String, termin: String): Result<IndenBookingAmbilKuitansiModel?>

    suspend fun insert(model: IndenBookingAmbilKuitansiModel): Result<Nothing?>

    suspend fun delete(keyId: String, termin: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}