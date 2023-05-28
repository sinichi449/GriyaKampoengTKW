package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi

interface AmbilKuitansiRepository {

    suspend fun get(kavling: String, termin: String): Result<AmbilKuitansi?>

    suspend fun insert(ambilKuitansi: AmbilKuitansi): Result<Nothing?>

}