package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel

interface RemoteKavlingCatatanPembayaranDataSource {

    suspend fun getCatatan(kavlingKode: String): Result<KavlingCatatanPembayaranModel?>

    suspend fun addCatatan(kavlingKode: String, kavlingCatatanPembayaranModel: KavlingCatatanPembayaranModel): Result<Nothing?>

    suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?>
}