package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel

interface LocalCatatanPembayaranDataSource {

    suspend fun getCatatan(kavlingKode: String): Result<CatatanPembayaranModel?>

    suspend fun addCatatan(kavlingKode: String, catatanPembayaranModel: CatatanPembayaranModel): Result<Nothing?>

    suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?>

    suspend fun updateCatatan(kavlingKode: String, oldData: CatatanPembayaranModel, newData: CatatanPembayaranModel): Result<Nothing?>

}