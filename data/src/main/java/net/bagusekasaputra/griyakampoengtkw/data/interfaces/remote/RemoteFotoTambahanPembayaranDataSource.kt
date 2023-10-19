package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahanPembayaranModel

interface RemoteFotoTambahanPembayaranDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaranModel?>

    suspend fun insert(model: FotoTambahanPembayaranModel): Result<Nothing?>

    suspend fun isFotoExist(kavling: String, id: String): Result<Boolean>

    suspend fun delete(kavling: String, id: String): Result<Nothing?>
}