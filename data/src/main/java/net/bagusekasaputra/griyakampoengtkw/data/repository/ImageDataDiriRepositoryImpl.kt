package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriUri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository

class ImageDataDiriRepositoryImpl(
    private val localImageDataDiri: LocalImageDataDiriDataSource,
    private val remoteImageDataDiri: RemoteImageDataDiriDataSource,
    private val backupImageDataDiri: BackupImageDataDiriDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val contentResolver: ContentResolver,
): ImageDataDiriRepository {

    private val metadataTable = "image_data_diri"
    // Check server metadata only ONCE
    // for getBatch() method.
    private var hasMetadataChecked = false

    private suspend fun getImageDataDiri(kavlingKode: String, onSuccess: (imageDataDiriModel: ImageDataDiriModel?) -> Unit) {
        if (!hasMetadataChecked) {
            Log.d("DEBUG_ME", "ImageDataDiriRepoImpl: Checking server Metadata now ...")
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
            } else {
                Log.d("DEBUG_ME", "ImageDataDiriRepo: Cache is VALID!")
            }

            hasMetadataChecked = true
        } else {
            Log.d("DEBUG_ME", "ImageDataDiriRepoImpl: Skipping check server Metadata ...")
        }

        // Emitting value
        Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Querying from local data source ...")
        val localModel = localImageDataDiri.getByKavlingKode(kavlingKode)
            .first()
        if (localModel == null) {
            Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Local data source is null, getting \"$kavlingKode\" from remote ...")
            remoteImageDataDiri.get(kavlingKode)?.let { remoteModel ->
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Uri from remote data source is ${remoteModel.imgUri}")
                localImageDataDiri.insert(remoteModel, true, {
                    Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully inserting image data diri on ${remoteModel.kavlingKode} to local data source")
                }, {
                    Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Failed to insert image data diri from remote: ${it?.message}")
                })
            }

            // Second try
            localImageDataDiri.getByKavlingKode(kavlingKode)
                .first { model ->
                    onSuccess(model)

                    true
                }
        } else {
            onSuccess(localModel)
            Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully fetch image data diri \"$kavlingKode\" from local data source.")
        }

    }

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri?>> {
        return callbackFlow {
            this@ImageDataDiriRepositoryImpl.getImageDataDiri(kavlingKode, onSuccess = {
                if (it != null) {
                    trySendBlocking(Result.success(mapImageDataDiri(it)))
                } else {
                    trySendBlocking(Result.success(null))
                }
            })

            awaitClose {  }
        }
    }

    override fun getBatchUri(listKavling: List<String>): Flow<Result<List<ImageDataDiriUri>?>> {
        return callbackFlow {
            val listImageDataDiriUri = mutableListOf<ImageDataDiriUri>()

            listKavling.forEach { kavling ->
                getImageDataDiri(kavling, onSuccess = { imageDataDiriModel ->
                    if (imageDataDiriModel != null) {
                        listImageDataDiriUri.add(
                            ImageDataDiriUri(
                                kavling = imageDataDiriModel.kavlingKode,
                                uriStr = imageDataDiriModel.imgUri,
                            )
                        )
                    }
                })
            }

            if (listImageDataDiriUri.isEmpty()) {
                trySendBlocking(Result.success(null))
            } else {
                trySendBlocking(Result.success(listImageDataDiriUri.toList()))
            }

            awaitClose {  }
        }
    }

    override fun getFromBackup(kavlingKode: String): Flow<Result<ImageDataDiri?>> {
        return callbackFlow {
            backupImageDataDiri.getImageDataDiri(kavlingKode)
                .onSuccess {
                    if (it == null) {
                        trySendBlocking(Result.success(null))
                    } else {
                        trySendBlocking(Result.success(mapImageDataDiri(it)))
                    }
                }
                .onFailure {
                    trySendBlocking(Result.failure(Throwable("Gagal mendapatkan ImageDataDiri dari Backup: ${it.cause}")))
                }

            awaitClose {  }
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean?>> {
        return flow {
            // Whenever changes occur in database, update the metadata
            updateRemoteMetadataOnWrite()

            val newModel = ImageDataDiriModel(kavlingKode, uri.toString())

            localImageDataDiri.insert(newModel, false,{
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
            // Whenever changes occur in database, update the metadata
            updateRemoteMetadataOnWrite()

            remoteImageDataDiri.delete(
                mapImageDataDiri(imageDataDiri.kavlingKode, Uri.EMPTY)
            ).onFailure {
                trySendBlocking(Result.failure(it))
            }

            localImageDataDiri.deleteByKavlingKode(imageDataDiri.kavlingKode)
                .onSuccess {
                    trySendBlocking(Result.success(true))
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

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

    private suspend fun updateRemoteMetadataOnWrite() {
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