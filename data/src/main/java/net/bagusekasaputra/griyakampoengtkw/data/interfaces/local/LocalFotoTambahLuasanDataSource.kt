package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahLuasanModel

interface LocalFotoTambahLuasanDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahLuasanModel?>

    suspend fun deleteAll(): Result<Nothing?>

    suspend fun insert(model: FotoTambahLuasanModel): Result<Nothing?>

    suspend fun delete(kavling: String, id: String): Result<Nothing?>

    suspend fun isExist(kavling: String, id: String): Result<Boolean>

}