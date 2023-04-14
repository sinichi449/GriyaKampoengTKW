package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapImageSpr
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupImageSPRDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSprUri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository

class ImageSprRepositoryImpl(
    private val localImageSpr: LocalImageSprDataSource,
    private val remoteImageSpr: RemoteImageSprDataSource,
    private val backupImageSprDataSource: BackupImageSPRDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val contentResolver: ContentResolver,
): ImageSprRepository {

    private val metadataTable = "image_spr"
    private var hasMetadataChecked = false

    private suspend fun getImageSpr(kavlingKode: String, onSuccess: (imageSprModel: ImageSprModel?) -> Unit) {
        if (!hasMetadataChecked) {
            Log.d("DEBUG_ME", "ImageSprRepoImpl: Checking server Metadata now ...")

            val localTimestamp = localMetadata.get(metadataTable)
                ?.timestamp
            val serverTimestamp = remoteMetadata.get(metadataTable)!!
                .timestamp
            val cacheInvalid = localTimestamp != serverTimestamp

            if (cacheInvalid) {
                Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Cache invalid!! Deleting all SPR cache ...")
                localImageSpr.deleteAll()
                    .onFailure {
                        it.printStackTrace()

                        Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Failed to invalidate (deleteAll) the local image spr: ${it.message}")
                    }
                localMetadata.insert(
                    MetadataModel(metadataTable, serverTimestamp)
                )
            }

            hasMetadataChecked = true
        } else {
            Log.d("DEBUG_ME", "ImageSprRepoImpl: Skipping check Metadata!")
        }

        // Emitting value
        Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Cache for SPR image is okay, getting from local data source now.")
        val localModel = localImageSpr.getByKavlingKode(kavlingKode).let {
            if (it.isSuccess) it.getOrNull()
            else {
                Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Failed to get Image SPR from local data source: ${it.exceptionOrNull()?.message}")
                null
            }
        }
        if (localModel == null) {
            Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Local Image SPR is still empty! Querying SPR for \"$kavlingKode\" to Remote Data Source now.")
            remoteImageSpr.get(kavlingKode)
                .onSuccess {  remoteModel ->
                    remoteModel?.let {
                        Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Uri from remote data source is ${it.dstUri}")
                        localImageSpr.insert(it, true, {
                            Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Success inserting image SPR in ${it.kavlingKode} to local data source!")
                        }, { cause ->
                            Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): FAILED to insert image SPR to local data source: ${cause?.message}")
                        })
                    }
                }
                .onFailure { cause ->
                    cause.printStackTrace()

                    Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): FAILED to query Image SPR \"$kavlingKode\" from remote : ${cause.message}")
                }

            // Second try
            localImageSpr.getByKavlingKode(kavlingKode)
                .onSuccess { model ->
                    onSuccess(model)
                }
                .onFailure {
                    it.printStackTrace()

                    Log.d("DEBUG_ME", "ImageSprRepoImpl:105 Failure second try getting Image SPR from local: ${it.cause}")

                    throw it
                }
        } else {
            onSuccess(localModel)

            Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully fetch image SPR \"$kavlingKode\" from local data source.")
        }
    }

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageSpr?>> {
        return callbackFlow {
            getImageSpr(kavlingKode, onSuccess = {
                if (it != null) Result.success(mapImageSpr(it, contentResolver))
                else Result.success(null)
            })

            awaitClose {  }
        }
    }

    override fun getFromBackup(kavlingKode: String): Flow<Result<ImageSpr?>> {
        return callbackFlow {
            backupImageSprDataSource.getImageSPR(kavlingKode)
                .onSuccess {
                    if (it != null) {
                        trySendBlocking(DataUtil.mapSingleResult(
                            originResult = Result.success(it),
                            targetMapper = { imageSprModel ->
                                mapImageSpr(imageSprModel, contentResolver)
                            },
                        ))
                    } else {
                        trySendBlocking(Result.success(null))
                    }
                }
                .onFailure {
                    trySendBlocking(Result.failure(Throwable("ImageSprRepoImpl:113 failed -> ${it.cause}")))
                }

            awaitClose {  }
        }
    }

    override fun getBatchUri(listKavling: List<String>): Flow<Result<List<ImageSprUri>?>> {
        return flow {
            val listImageSprUri = mutableListOf<ImageSprUri>()

            listKavling.forEach { kavling ->
                getImageSpr(kavling, onSuccess = {
                    if (it != null) listImageSprUri.add(mapImageSpr(it))
                })
            }

            emit(Result.success(listImageSprUri.toList()))
        }
    }

    override fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata()

            val newModel = ImageSprModel(kavlingKode, uri.toString())

            localImageSpr.insert(newModel, false, {
                Log.d("DEBUG_ME", "ImageSprRepoImpl->addImage(): Success adding image SPR \"${newModel.getFilename()}\" into local data source.")
            }, {
                Log.d("DEBUG_ME", "ImageSprRepoImpl->addImage(): FAILED adding Image SPR \"${newModel.getFilename()} into local data source: ${it?.message}")
            })

            emitAll(remoteImageSpr.insert(newModel))
        }
    }

    private suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable)
            ?: MetadataModel(metadataTable, currentTimemillis)
        val newMetadata = MetadataModel(metadataTable, currentTimemillis)

        remoteMetadata.update(oldMetadata, newMetadata)
        localMetadata.insert(newMetadata)
    }

}