package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranTambahLuasanModel

interface RemotePembayaranTambahLuasanDataSource {

    suspend fun getAll(kavling: String): Result<List<PembayaranTambahLuasanModel>?>

    suspend fun add(model: PembayaranTambahLuasanModel): Result<Nothing?>

    suspend fun deleteById(kavling: String, id: String): Result<Nothing?>

}