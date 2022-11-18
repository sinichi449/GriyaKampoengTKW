package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

interface LocalFotoPembayaranDataSource {

    suspend fun getFotoPembayaran(kavlingKode: String, termin: String): Result<FotoPembayaranModel?>

    suspend fun addFotoPembayaran(fotoPembayaranModel: FotoPembayaranModel): Result<Nothing?>

    suspend fun deleteById(id: Long): Result<Nothing?>

    suspend fun deleteByKavlingKodeAndTermin(kavlingKode: String, termin: String): Result<Nothing?>

    suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel,
    ): Result<Nothing?>
}