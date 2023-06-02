package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi

interface StandardAmbilKuitansiRepository {

    suspend fun get(kavling: String, termin: String): Result<StandardAmbilKuitansi?>

    suspend fun insert(standardAmbilKuitansi: StandardAmbilKuitansi): Result<Nothing?>

    suspend fun delete(kavling: String, termin: String): Result<Nothing?>
}