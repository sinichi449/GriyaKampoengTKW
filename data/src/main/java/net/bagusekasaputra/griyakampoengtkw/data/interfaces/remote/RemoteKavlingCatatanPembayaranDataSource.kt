package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel

interface RemoteKavlingCatatanPembayaranDataSource {

    suspend fun getCatatan(kavlingKode: String): Result<CatatanPembayaranModel?>

    suspend fun addCatatan(kavlingKode: String, catatanPembayaranModel: CatatanPembayaranModel): Result<Nothing?>

    suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?>
}