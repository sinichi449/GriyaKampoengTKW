package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

interface RemoteImageSprDataSource {

    suspend fun get(kavlingKode: String): Result<ImageSprModel?>

    fun insert(newModel: ImageSprModel): Flow<Result<Nothing?>>

}