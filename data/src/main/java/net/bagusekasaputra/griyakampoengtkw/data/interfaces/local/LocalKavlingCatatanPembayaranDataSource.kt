package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel

interface LocalKavlingCatatanPembayaranDataSource {

    suspend fun getCatatan(kavlingKode: String): Result<KavlingCatatanPembayaranModel?>

    suspend fun addCatatan(kavlingKode: String, kavlingCatatanPembayaranModel: KavlingCatatanPembayaranModel): Result<Nothing?>

    suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?>

    suspend fun updateCatatan(kavlingKode: String, oldData: KavlingCatatanPembayaranModel, newData: KavlingCatatanPembayaranModel): Result<Nothing?>

}