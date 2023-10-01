package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.TambahanPembayaranModel

interface RemoteTambahanPembayaranDataSource {

    suspend fun getAll(kavling: String): Result<List<TambahanPembayaranModel>?>

    suspend fun getById(kavling: String, id: String): Result<TambahanPembayaranModel?>

    suspend fun insert(model: TambahanPembayaranModel): Result<Nothing?>

    suspend fun update(kavling: String, id: String, newData: TambahanPembayaranModel): Result<Nothing?>

    suspend fun delete(kavling: String, id: String): Result<Nothing?>

}