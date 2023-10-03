package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahanPembayaranModel

interface LocalFotoTambahanPembayaranDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaranModel?>

    suspend fun insert(model: FotoTambahanPembayaranModel, fromRemote: Boolean): Result<Nothing?>

}