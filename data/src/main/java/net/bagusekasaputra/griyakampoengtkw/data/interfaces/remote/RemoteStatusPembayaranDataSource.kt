package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.StatusPembayaranModel

interface RemoteStatusPembayaranDataSource {

    suspend fun get(kavling: String): Result<StatusPembayaranModel?>

    suspend fun insert(model: StatusPembayaranModel): Result<Nothing?>
}