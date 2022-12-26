package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository

class ImageSprRepositoryImpl(
    private val localImageSpr: LocalImageSprDataSource,
    private val remoteImageSpr: RemoteImageSprDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val contentResolver: ContentResolver,
): ImageSprRepository {

    private val metadataTable = "image_spr"

    override fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageSpr?>> {
        return flow {
            // Cache validation
            val localTimestamp = localMetadata.get(metadataTable)
                ?.timestamp
            val serverTimestamp = remoteMetadata.get(metadataTable)!!
                .timestamp
            val cacheInvalid = localTimestamp != serverTimestamp

            if (cacheInvalid) {
                Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Cache invalid!! Deleting all SPR cache ...")
                localImageSpr.deleteAll()
                    .onFailure {
                        Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): Failed to invalidate (deleteAll) the local image spr: ${it.message}")
                    }
                localMetadata.insert(
                    MetadataModel(metadataTable, serverTimestamp)
                )
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
                        Log.d("DEBUG_ME", "ImageSprRepoImpl->get(): FAILED to query Image SPR \"$kavlingKode\" from remote : ${cause.message}")
                    }

                // Second try
                localImageSpr.getByKavlingKode(kavlingKode)
                    .onSuccess { model ->
                        emit(Result.success(
                            if (model != null) mapImageSpr(model)
                            else null
                        ))
                    }
                    .onFailure {
                        emit(Result.failure(it))
                    }
            } else {
                emit(Result.success(mapImageSpr(localModel)))
                Log.d("DEBUG_ME", "ImageDataDiriRepo->get(): Successfully fetch image SPR \"$kavlingKode\" from local data source.")
            }
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

    private fun mapImageSpr(imageSprModel: ImageSprModel): ImageSpr {
        return imageSprModel.let {
            ImageSpr(
                kavlingKode = it.kavlingKode,
                bitmap = ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.dstUri))
            )
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

    private fun mapImageSpr(imageSpr: ImageSpr, dstUri: String): ImageSprModel {
        return imageSpr.let {
            ImageSprModel(
                kavlingKode = it.kavlingKode,
                dstUri = dstUri,
            )
        }
    }

}