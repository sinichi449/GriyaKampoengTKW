package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageSpr

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

interface LocalImageSprDataSource {

    suspend fun getByKavlingKode(
        kavlingKode: String,
        onSuccess: (imageSprModel: ImageSprModel) -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun insert(
        imageSprModel: ImageSprModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

}