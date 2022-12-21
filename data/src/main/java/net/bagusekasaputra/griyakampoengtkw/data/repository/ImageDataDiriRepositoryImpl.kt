package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class ImageDataDiriRepositoryImpl(
    private val localImageDataDiri: LocalImageDataDiriDataSource,
    private val remoteImageDataDiri: RemoteImageDataDiriDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val contentResolver: ContentResolver,
): ImageDataDiriRepository {

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri?>> {
        return flow {
            // Cache validation
            val metadataTable = "image_data_diri"
            val localTimestamp = localMetadata.get(metadataTable)
                ?.timestamp
            val serverTimestamp = remoteMetadata.get(metadataTable)!!
                .timestamp
            val cacheInvalid = localTimestamp != serverTimestamp

            if (cacheInvalid) {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Cache invalid!! Deleting all cache ...")
                localImageDataDiri.deleteAll()
                localMetadata.insert(
                    MetadataModel(metadataTable, serverTimestamp)
                )
            }

            // Emitting value
            Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Cache for image data diri \"$kavlingKode\" is okay, Querying from local data source ...")
            val localModel = localImageDataDiri.getByKavlingKode(kavlingKode)
                .first()
            if (localModel == null) {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Local data source is null, getting \"$kavlingKode\" from remote ...")
                remoteImageDataDiri.get(kavlingKode)?.let { remoteModel ->
                    localImageDataDiri.insert(remoteModel, {}, {})
                }

                // Second try
                localImageDataDiri.getByKavlingKode(kavlingKode)
                    .first { model ->
                        model?.let {
                            emit(Result.success(mapImageDataDiri(it)))
                        }
                        true
                    }
            } else {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully fetch image data diri \"$kavlingKode\" from local data source.")
                emit(Result.success(mapImageDataDiri(localModel)))
            }
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean>> {
        return callbackFlow {
            val model = mapImageDataDiri(kavlingKode, uri)
            localImageDataDiri.insert(
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
            localImageDataDiri.getUriByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = {
                    it.toFile().delete()
                },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan mendapatkan ID")))}
            )

            localImageDataDiri.deleteByKavlingKode(
                kavlingKode = imageDataDiri.kavlingKode,
                onSuccess = { trySendBlocking(Result.success(true)) },
                onFailure = { trySendBlocking(Result.failure(it ?: UnknownError("Terjadi kesalahan menghapus gambar"))) },
            )

            awaitClose {  }
        }
    }

    override fun getUriByKavlingKode(kavlingKode: String): Flow<Result<Uri>> {
        return callbackFlow {


            localImageDataDiri.getUriByKavlingKode(
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