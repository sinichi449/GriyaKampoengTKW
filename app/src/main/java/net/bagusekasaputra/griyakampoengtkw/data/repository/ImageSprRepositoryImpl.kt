package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageSpr.LocalImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageSprRepositoryImpl @Inject constructor(
    private val localImageSprDataSource: LocalImageSprDataSource,
    private val contentResolver: ContentResolver,
): ImageSprRepository {

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageSpr?>> {
        return callbackFlow {
            localImageSprDataSource.getByKavlingKode(
                kavlingKode = kavlingKode,
                onSuccess = { trySendBlocking(Result.success(mapImageSpr(it))) },
                onFailure = { trySendBlocking(Result.failure(it ?: Exception("Unknown error: terjadi kegagalan mendapatkan foto SPR"))) },
            )

            awaitClose { }
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Nothing?>> {
        return callbackFlow {
            localImageSprDataSource.insert(
                imageSprModel = ImageSprModel(kavlingKode, uri.toString()),
                onSuccess = { trySendBlocking(Result.success(null)) },
                onFailure = { trySendBlocking(Result.failure(it ?: Exception("Unknown error: terjadi kegagalan menambahkan foto SPR"))) },
            )

            awaitClose {  }
        }
    }

    private fun mapImageSpr(imageSprModel: ImageSprModel): ImageSpr {
        return imageSprModel.let {
            ImageSpr(
                kavlingKode = it.kavlingKode,
                bitmap = net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.dstUri))
            )
        }
    }

    private fun mapImageSpr(imageSpr: ImageSpr, dstUri: String): ImageSprModel {
        return imageSpr.let {
            ImageSprModel(
                kavlingKode = it.kavlingKode,
                dstUri = dstUri,
            )
        }
    }

}