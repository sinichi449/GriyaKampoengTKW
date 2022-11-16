package net.bagusekasaputra.griyakampoengtkw.data.source.remote.catatanPembayaran

import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel

interface RemoteCatatanPembayaranDataSource {

    suspend fun getCatatan(kavlingKode: String): Result<CatatanPembayaranModel?>

    suspend fun addCatatan(kavlingKode: String, catatanPembayaranModel: CatatanPembayaranModel): Result<Nothing?>

    suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?>
}