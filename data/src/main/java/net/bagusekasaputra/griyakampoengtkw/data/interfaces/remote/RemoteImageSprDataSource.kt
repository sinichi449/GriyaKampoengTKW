package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

interface RemoteImageSprDataSource {

    suspend fun get(kavlingKode: String): Result<ImageSprModel?>

}