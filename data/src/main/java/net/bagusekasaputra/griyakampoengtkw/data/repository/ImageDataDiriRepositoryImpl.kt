package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class ImageDataDiriRepositoryImpl(
    private val localImageDataDiriDataSource: LocalImageDataDiriDataSource,
    private val contentResolver: ContentResolver,
): ImageDataDiriRepository {

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri?>> {
        return flow {
            val localImageDataDiri = localImageDataDiriDataSource.getByKavlingKode(kavlingKode)
                .map {
                    if (it == null) Result.success(null)
                    else Result.success(mapImageDataDiri(imageDataDiriModel = it))
                }

            emitAll(localImageDataDiri)
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean>> {
        return callbackFlow {
            val model = mapImageDataDiri(kavlingKode, uri)
            localImageDataDiriDataSource.insert(
                imageDataDiriModel = model,
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan menambahkan gambar"))) },
            )

            awaitClose {  }
        }
    }

    override fun updateImage(
        oldImageDataDiri: ImageDataDiri,
        newImageDataDiri: ImageDataDiri,
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteImage(imageDataDiri: ImageDataDiri): Flow<Result<Boolean>> {
        return callbackFlow {
            localImageDataDiriDataSource.getUriByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = {
                    it.toFile().delete()
                },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan mendapatkan ID")))}
            )

            localImageDataDiriDataSource.deleteByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan menghapus gambar"))) },
            )

            awaitClose {  }
        }
    }

    override fun getUriByKavlingKode(kavlingKode: String): Flow<Result<Uri>> {
        return callbackFlow {


            localImageDataDiriDataSource.getUriByKavlingKode(
                kavlingKode = kavlingKode,
                onSuccess = { trySendBlocking(Result.success(it)) },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Gagal mendapatkan uri"))) },
            )

            awaitClose {  }
        }
    }

    private fun mapImageDataDiri(imageDataDiriModel: ImageDataDiriModel): ImageDataDiri {
        return imageDataDiriModel.let {
            ImageDataDiri(
                kavlingKode = it.kavlingKode,
                bitmap = ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.imgUri))
            )
        }
    }

    private fun mapImageDataDiri(kavlingKode: String, uri: Uri): ImageDataDiriModel {
        return ImageDataDiriModel(
            kavlingKode = kavlingKode,
            imgUri = uri.toString(),
        )
    }


}