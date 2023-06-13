package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.Cacheable
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel

interface RemotePengembalianDataSource: Cacheable {

    suspend fun get(keyId: String): Result<PengembalianModel?>

    suspend fun getKeyIds(): Result<List<String>?>

    suspend fun insert(model: PengembalianModel): Result<Unit>

    suspend fun update(keyId: String, newModel: PengembalianModel): Result<Unit>

    suspend fun delete(keyId: String): Result<Unit>

    /**
     * Download the image associated with [PengembalianModel].
     *
     * **NOTE:** You must catch any [Exception] inside your implementation, since it doesn't
     * return [Result], or else the [Exception] throwed will uncaught.
     *
     * @return [Boolean] `true` if the image given [keyId] does exist and successfully downloaded into [saveUri],
     * or `false` if either image doesn't exist or download process failed.
     */
    suspend fun downloadImage(keyId: String,  saveUri: String): Boolean
}