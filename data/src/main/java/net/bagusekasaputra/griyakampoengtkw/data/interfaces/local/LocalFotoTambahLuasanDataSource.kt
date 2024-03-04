package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahLuasanModel

interface LocalFotoTambahLuasanDataSource {

    suspend fun get(kavling: String, id: String): Result<FotoTambahLuasanModel?>

    suspend fun deleteAll(): Result<Nothing?>



}