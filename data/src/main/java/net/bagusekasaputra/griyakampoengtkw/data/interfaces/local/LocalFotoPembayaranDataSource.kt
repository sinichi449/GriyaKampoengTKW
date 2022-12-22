package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

interface LocalFotoPembayaranDataSource {

    suspend fun getFotoPembayaran(kavlingKode: String, termin: String): Result<FotoPembayaranModel?>

    suspend fun addFotoPembayaran(fotoPembayaranModel: FotoPembayaranModel, fromRemote: Boolean): Result<Nothing?>

    suspend fun deleteById(id: Long): Result<Nothing?>

    suspend fun deleteByKavlingKodeAndTermin(kavlingKode: String, termin: String): Result<Nothing?>

    suspend fun deleteAllFotoPembayaran(kavlingKode: String): Result<Nothing?>

    suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel,
    ): Result<Nothing?>

    suspend fun getFotoUri(kavlingKode: String, termin: String): Result<Uri?>

    suspend fun deleteAll(kavlingKode: String)
}