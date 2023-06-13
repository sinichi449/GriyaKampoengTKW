package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel

interface LocalPengembalianDataSource: Cacheable {

    suspend fun get(keyId: String): Result<PengembalianModel?>

    suspend fun getKeyIds(): Result<List<String>?>

    suspend fun insert(model: PengembalianModel): Result<Unit>

    suspend fun insertAll(models: List<PengembalianModel>): Result<Unit>

    suspend fun update(keyId: String, newModel: PengembalianModel): Result<Unit>

    suspend fun delete(keyId: String): Result<Unit>

    suspend fun invalidate(): Result<Unit>

}