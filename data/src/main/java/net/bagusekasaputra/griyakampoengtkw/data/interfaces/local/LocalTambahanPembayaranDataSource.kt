package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.TambahanPembayaranModel

interface LocalTambahanPembayaranDataSource {

    suspend fun getAll(kavling: String): Result<List<TambahanPembayaranModel>?>

    suspend fun getById(kavling: String, id: String): Result<TambahanPembayaranModel?>

    suspend fun insert(model: TambahanPembayaranModel): Result<Nothing?>

    suspend fun insertAll(models: List<TambahanPembayaranModel>): Result<Nothing?>

    suspend fun update(kavling: String, id: String, newData: TambahanPembayaranModel): Result<Nothing?>

    suspend fun delete(kavling: String, id: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}