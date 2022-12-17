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
import net.bagusekasaputra.griyakampoengtkw.data.util.networkBoundResource
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
            val metadataTable = "image_data_diri"
            val serverTimestamp = remoteMetadata
                .get(metadataTable)!!
                .timestamp

            emitAll(networkBoundResource<Result<ImageDataDiriModel?>, ImageDataDiriModel?>(
                query = {
                    Log.d("DEBUG_ME", "repo->getImageDataDiri(): Querying image $kavlingKode from local storage ...")
                    localImageDataDiri.getByKavlingKode(kavlingKode).map {
                        if (it == null) Result.success(null)
                        else Result.success(it)
                    }
                },
                fetch = {
                    Log.d("DEBUG_ME", "repo->getImageDataDiri(): Fetching from remote server for $kavlingKode")
                    remoteImageDataDiri.get(kavlingKode)
                },
                shouldFetch = {
                    val localTimeStamp = localMetadata.get(metadataTable)
                        ?.timestamp

                    ((localTimeStamp == null) or (serverTimestamp != localTimeStamp))
                },
                saveFetchResult = { model ->
                    model?.let {
                        Log.d("DEBUG_ME", "repo->getImageDataDiri(): Saving fetch result $kavlingKode ...")

                        // Insert new metadata
                        localMetadata.insert(
                            MetadataModel(tableName = metadataTable, timestamp = serverTimestamp)
                        )

                        // Purge all local data
                        localImageDataDiri.deleteAll()

                        // Insert fresh data from remote
                        localImageDataDiri.insert(
                            imageDataDiriModel = it,
                            onSuccess = {},
                            onFailure = { throwable -> Log.d("DEBUG_ME", "Failed to insert image data diri to local: ${throwable?.message}")},
                        )
                    }
                }
            ).map { resource ->
                resource.data!!.map { model ->
                    model?.let {
                        mapImageDataDiri(it)
                    }
                }
            })
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