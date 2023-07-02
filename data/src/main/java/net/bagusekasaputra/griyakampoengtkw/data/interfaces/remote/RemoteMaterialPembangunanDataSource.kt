package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable
import net.bagusekasaputra.griyakampoengtkw.data.model.MaterialPembangunanModel

interface RemoteMaterialPembangunanDataSource: Cacheable {

    suspend fun getAll(untuk: String, kategori: String): Result<List<MaterialPembangunanModel>?>

    suspend fun insert(model: MaterialPembangunanModel): Result<Unit>

    suspend fun delete(kategori: String, target: String, keyId: String): Result<Unit>

    suspend fun update(
        kategori: String,
        target: String,
        keyId: String,
        newData: MaterialPembangunanModel
    ): Result<Unit>

}