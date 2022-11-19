package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.LocalImageDataDiriSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDataDiriRepositoryImpl @Inject constructor(
    private val localImageDataDiriSource: LocalImageDataDiriSource,
    private val contentResolver: ContentResolver,
): ImageDataDiriRepository {

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri>> {
        return callbackFlow {
            localImageDataDiriSource.getByKavlingKode(
                kavlingKode = kavlingKode,
                onSuccess = { trySendBlocking(Result.success(mapImageDataDiri(it))) },
                onFailure = { trySendBlocking(Result.failure(it?: UnknownError("Terjadi kesalahan mendapatkan gambar"))) }
            )

            awaitClose {  }
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean>> {
        return callbackFlow {
            val model = mapImageDataDiri(kavlingKode, uri)
            localImageDataDiriSource.insert(
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
            localImageDataDiriSource.getUriByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = {
                    it.toFile().delete()
                },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan mendapatkan ID")))}
            )

            localImageDataDiriSource.deleteByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan menghapus gambar"))) },
            )

            awaitClose {  }
        }
    }

    override fun getUriByKavlingKode(kavlingKode: String): Flow<Result<Uri>> {
        return callbackFlow {


            localImageDataDiriSource.getUriByKavlingKode(
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
                bitmap = net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.imgUri))
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