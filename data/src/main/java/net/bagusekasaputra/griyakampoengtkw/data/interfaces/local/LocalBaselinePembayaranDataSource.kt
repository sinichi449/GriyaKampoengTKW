package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel

interface LocalBaselinePembayaranDataSource {

    suspend fun get(kavling: String): Result<BaselinePembayaranModel?>

    suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}