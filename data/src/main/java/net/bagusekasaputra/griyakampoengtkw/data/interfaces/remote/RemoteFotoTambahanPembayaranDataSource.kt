package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahanPembayaranModel

interface RemoteFotoTambahanPembayaranDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaranModel?>

    suspend fun insert(model: FotoTambahanPembayaranModel): Result<Nothing?>

}