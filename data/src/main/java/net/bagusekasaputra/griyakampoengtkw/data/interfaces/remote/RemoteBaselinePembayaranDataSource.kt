package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel

interface RemoteBaselinePembayaranDataSource {

    suspend fun get(kavling: String): Result<BaselinePembayaranModel?>

    suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?>

}