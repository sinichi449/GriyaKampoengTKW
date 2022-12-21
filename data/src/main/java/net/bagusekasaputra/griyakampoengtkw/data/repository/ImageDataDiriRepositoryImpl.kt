package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
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

    private val metadataTable = "image_data_diri"

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri?>> {
        return flow {
            // Cache validation
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
                        emit(Result.success(
                            if (model != null) mapImageDataDiri(model)
                            else null
                        ))
                        true
                    }
            } else {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully fetch image data diri \"$kavlingKode\" from local data source.")
                emit(Result.success(mapImageDataDiri(localModel)))
            }
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean?>> {
        return flow {
            // Whenever changes occur in database, update the metadata
            updateMetadata()

            val newModel = ImageDataDiriModel(kavlingKode, uri.toString())

            localImageDataDiri.insert(newModel, {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->addImages(): Success adding image in $kavlingKode")
            }, {
                Log.d("DEBUG_ME", "ImageDataDiriRepo->addImages(): Error : ${it?.message}")
            })

            emitAll(remoteImageDataDiri.insert(newModel))
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

    private suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable)!!
        val newMetadataModel = MetadataModel(metadataTable, currentTimemillis)

        remoteMetadata.update(oldMetadata, newMetadataModel)
        localMetadata.insert(newMetadataModel)
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