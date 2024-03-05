package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahLuasanModel

interface RemoteFotoTambahLuasanDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahLuasanModel?>

    suspend fun insert(model: FotoTambahLuasanModel): Result<Nothing?>

    suspend fun delete(kavling: String, id: String): Result<Nothing?>
}