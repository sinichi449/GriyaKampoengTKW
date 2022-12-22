package net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

class StorageImageDataDiriDataSource(
    storageReference: StorageReference,
    private val externalFilesDir: File?,
): RemoteImageDataDiriDataSource {

    private val imageDataDiriRef = storageReference.child(FirebaseNodes.IMAGE_DATA_DIRI)

    override suspend fun get(kavlingKode: String): ImageDataDiriModel? {
        return callbackFlow {
            val dstDir = File(externalFilesDir, ImageDataDiriModel.DST_FOLDER)

            val name = getFileName(kavlingKode)
            val file = File(dstDir, name)

            Log.d("DEBUG_ME", "StorageImage->get(): Saving \"$name\" to ${file.toUri()}")

            imageDataDiriRef.child(name)
                .getFile(file)
                .addOnProgressListener {
                    Log.d("DEBUG_ME", "StorageImage->get(): Downloading \"$name\" ${it.bytesTransferred.toMegaBytes()}/${it.totalByteCount.toMegaBytes()} MB ...")
                }
                .addOnCompleteListener {
                    Log.d("DEBUG_ME", "StorageImage->get(): Download $name is completed!")
                    if (file.exists()) {
                        trySendBlocking(
                            ImageDataDiriModel(
                                kavlingKode = kavlingKode,
                                imgUri = file.toUri().toString(),
                            )
                        )
                    } else {
                        Log.d("DEBUG_ME", "StorageImageDataDiri->get(): Resulting download file $name not found!!")
                        trySendBlocking(null)
                    }
                }
                .addOnFailureListener {
                    Log.d("DEBUG_ME", "StorageImage->get(): Failed to download \"$name\" : ${it.message}")

                    trySendBlocking(null)
                }

            awaitClose {
                Log.d("DEBUG_ME", "StorageImage->get(): Download \"$name\" completed (2) Saving to local ...")
            }
        }.first()
    }

    override fun insert(imageDataDiriModel: ImageDataDiriModel): Flow<Result<Boolean?>> {
        return callbackFlow {
            val uri = File(externalFilesDir, imageDataDiriModel.getFullPath())
                .toUri()
            Log.d("DEBUG_ME", "StorageImage->insert(): Prepare to upload $uri ...")

            val uploadTask = imageDataDiriRef.child(imageDataDiriModel.getFilename())
                .putFile(uri)
                .addOnProgressListener {
                    Log.d("DEBUG_ME", "StorageImage->insert(): Uploading \"${imageDataDiriModel.getFullPath()}\" ${it.bytesTransferred.toMegaBytes()}/${it.totalByteCount.toMegaBytes()} MB ...")
                }
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    Log.d("DEBUG_ME", "StorageImageDataDiri->insert(): Error uploading ${imageDataDiriModel.getFilename()}: ${it.message}")
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {
                if (!uploadTask.isComplete) {
                    uploadTask.cancel()
                }
            }
        }
    }

    override suspend fun update(
        oldModel: ImageDataDiriModel,
        newModel: ImageDataDiriModel
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(imageDataDiriModel: ImageDataDiriModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val filename = "${imageDataDiriModel.kavlingKode}_data_diri.png"

            imageDataDiriRef.child(filename)
                .delete()
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private fun getFileName(kavlingKode: String) = "${kavlingKode}_data_diri.png"

    private fun Long.toMegaBytes() = if (this > 0L)
            BigDecimal(1024 * 1024).let { aMegabyte ->
                BigDecimal(this).divide(aMegabyte, 2, RoundingMode.HALF_UP)
            }.toDouble()
        else
            0.0
}