package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
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
import java.io.File

class ImageDataDiriRepositoryImpl(
    private val localImageDataDiri: LocalImageDataDiriDataSource,
    private val remoteImageDataDiri: RemoteImageDataDiriDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val externalFileDir: File?,
    private val contentResolver: ContentResolver,
    private val cacheHelper: CacheHelper,
): ImageDataDiriRepository {

    private val metadataTable = "image_data_diri"
    private val imageIndenBookingLocalTable = "fotoIdentitasIndenBooking"
    private val imageIndenBookingRemoteTable = "indenBooking/imageDataDiri"
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
        TODO("Not yet implemented")
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


    /**
     * Inden Booking related
     */
    override suspend fun getFromIndenBooking(keyId: String): Result<Uri?> {
        val isInvalidCache = cacheHelper.checkAndInvalidateCache(
            imageIndenBookingLocalTable,
            imageIndenBookingRemoteTable,
            onInvalid = {
                // Delete all file cache in external storage
                val dstFile = File(externalFileDir, "inden_booking_images/data_diri_images")
                if (dstFile.exists()) {
                    dstFile.deleteRecursively()
                }

                localImageDataDiri.deleteAllFromIndenBooking()
            }
        )
        val localModel = localImageDataDiri.getFromIndenBooking(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (isInvalidCache || localModel == null) {
            Log.d("INDEN_BOOKING", "Foto Identitas on Local Data Source either invalidated or null!" +
                    " Fetching from Remote Data Source now.")

            val remoteModel = remoteImageDataDiri.getFromIndenBooking(keyId).getOrThrow()
            remoteModel?.also {
                localImageDataDiri.insertFromIndenBooking(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Foto Identitas returning from Local Data Source!")
        }

        return localImageDataDiri.getFromIndenBooking(keyId)
    }

    override suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            Log.d("INDEN_BOOKING", "Begin insertion Foto Identitas for keyId $keyId ...")

            // Copy to appropriate directory and delete the image leftover
            Log.d("INDEN_BOOKING", "Copying foto identitas and deleting leftover for $keyId...")
            val newUri = try {
                moveIndenBookingFileAndDeleteImagePickerLeftover(keyId, uri)
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))

                null
            }

            // Remote Insertion
            Log.d("INDEN_BOOKING", "Remote insertion for Foto Identitas $keyId ...")
            remoteImageDataDiri.insertFromIndenBooking(keyId, newUri!!)
                .onSuccess {
                    // Update cache
                    Log.d("INDEN_BOOKING", "Updating cache for Foto Identitas insertion $keyId...")
                    cacheHelper.updateMetadata(imageIndenBookingLocalTable, imageIndenBookingRemoteTable)
                        .onSuccess {
                            // Local Insertion
                            Log.d("INDEN_BOOKING", "Local insertion for Foto Identitas $keyId ...")
                            localImageDataDiri.insertFromIndenBooking(keyId, newUri)
                                .onSuccess {
                                    Log.d("INDEN_BOOKING", "Success adding Foto Identitas for $keyId !")
                                    trySendBlocking(Result.success(null))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }
            awaitClose {  }
        }.first()
    }

    override suspend fun updateFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            Log.d("INDEN_BOOKING", "Begin update Foto Identitas for keyId $keyId ...")

            // Copy to appropriate directory and delete the image leftover
            Log.d("INDEN_BOOKING", "Copying foto identitas and deleting leftover for $keyId...")
            val newUri = try {
                moveIndenBookingFileAndDeleteImagePickerLeftover(keyId, uri)
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))

                null
            }

            // Remote Update
            Log.d("INDEN_BOOKING", "Remote update for Foto Identitas $keyId ...")
            remoteImageDataDiri.updateFromIndenBooking(keyId, newUri!!)
                .onSuccess {
                    // Update cache
                    Log.d("INDEN_BOOKING", "Updating cache for Foto Identitas update $keyId...")
                    cacheHelper.updateMetadata(imageIndenBookingLocalTable, imageIndenBookingRemoteTable)
                        .onSuccess {
                            // Local Update
                            Log.d("INDEN_BOOKING", "Local update for Foto Identitas $keyId ...")
                            localImageDataDiri.updateFromIndenBooking(keyId, newUri)
                                .onSuccess {
                                    Log.d("INDEN_BOOKING", "Success updating Foto Identitas for $keyId !")
                                    trySendBlocking(Result.success(null))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }
            awaitClose {  }
        }.first()
    }

    override suspend fun deleteFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // Delete from cache external storage first
            try {
                uri.toFile().delete()
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            // Remote Deletion
            remoteImageDataDiri.deleteFromIndenBooking(keyId)
                .onSuccess {
                    // Cache update
                    cacheHelper.updateMetadata(imageIndenBookingLocalTable, imageIndenBookingRemoteTable)
                        .onSuccess {
                            // Local Deletion
                            localImageDataDiri.deleteFromIndenBooking(keyId)
                                .onSuccess {
                                    trySendBlocking(Result.success(null))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
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

    private fun moveIndenBookingFileAndDeleteImagePickerLeftover(
        keyId: String,
        imagePickerUri: Uri,
    ): Uri {
        val fileName = "${keyId}.png"
        val dstFile = File(externalFileDir, "inden_booking_images/data_diri_images/$fileName")
        val srcFile = imagePickerUri.toFile()

        srcFile.renameTo(dstFile)

        return dstFile.toUri()
    }
}