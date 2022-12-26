package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

interface LocalImageSprDataSource {

    suspend fun getByKavlingKode(kavlingKode: String): Result<ImageSprModel?>

    suspend fun insert(
        imageSprModel: ImageSprModel,
        fromRemote: Boolean,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun deleteAll(): Result<Nothing?>

}