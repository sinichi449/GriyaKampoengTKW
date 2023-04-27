package net.bagusekasaputra.griyakampoengtkw.data.remote.imageSpr

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File

class StorageImageSprDataSource(
    storageReference: StorageReference,
    private val externalFilesDir: File?,
): RemoteImageSprDataSource {

    private val imageSprRef = storageReference.child(FirebaseNodes.IMAGE_SPR)
    private val LOG_TAG = "DEBUG_ME"
    private val LOG_MESSAGE = { func: String, msg: String ->
        "StorageImageSpr->$func(): $msg"
    }

    override suspend fun get(kavlingKode: String): Result<ImageSprModel?> {
        return callbackFlow<Result<ImageSprModel?>> {
            val rootDir = File(externalFilesDir, ImageSprModel.DST_ROOT)

            val model = ImageSprModel(kavlingKode = kavlingKode, dstUri = "")
            val filename = model.getFilename()
            val dstFile = File(rootDir, filename)

            Log.d(LOG_TAG, LOG_MESSAGE("get", "Saving \"$filename\" into ${dstFile.toUri()}"))

            imageSprRef.child(filename)
                .getFile(dstFile)
                .addOnProgressListener {
                    Log.d(LOG_TAG, LOG_MESSAGE("get", "Downloading $filename is ${it.bytesTransferred} / ${it.totalByteCount} bytes"))
                }
                .addOnCompleteListener {
                    Log.d(LOG_TAG, LOG_MESSAGE("get", "Successfully downloaded $filename !!"))
                    if (dstFile.exists()) {
                        trySendBlocking(
                            Result.success(
                                ImageSprModel(
                                    kavlingKode = kavlingKode,
                                    dstUri = dstFile.toUri().toString(),
                                )
                            )
                        )
                    } else {
                        Log.d(LOG_TAG, LOG_MESSAGE("get", "Resulting download for \"$filename\" is not found!"))
                        trySendBlocking(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    Log.d(LOG_TAG, LOG_MESSAGE("get", "Failed to download \"$filename\" : ${it.message}"))
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {
                Log.d(LOG_TAG, LOG_MESSAGE("get", "Connection to storage of Image SPR closed."))
            }
        }.first()
    }

    override fun insert(newModel: ImageSprModel): Flow<Result<Nothing?>> {
        return callbackFlow {
            try {
                val uri = File(externalFilesDir, newModel.getFullPath())
                    .toUri()
                Log.d(LOG_TAG, LOG_MESSAGE("insert", "Preparing to upload $uri ..."))

                imageSprRef.child(newModel.getFilename())
                    .putFile(uri)
                    .addOnProgressListener {
                        Log.d(LOG_TAG,
                            LOG_MESSAGE("insert",
                                "Uploading \"${newModel.getFilename()} is ${it.bytesTransferred} / ${it.totalByteCount} bytes"))
                    }
                    .addOnCompleteListener {
                        Log.d(LOG_TAG,
                            LOG_MESSAGE("insert", "SUCCESS uploading ${newModel.getFilename()} !!"))
                        trySendBlocking(Result.success(null))
                    }
                    .addOnFailureListener {
                        Log.d(LOG_TAG,
                            LOG_MESSAGE("insert",
                                "FAILED to upload ${newModel.getFilename()} : ${it.message}"))
                        trySendBlocking(Result.failure(it))
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(LOG_TAG, LOG_MESSAGE("insert", "Error occured attempt at uploading ${newModel.getFilename()} : ${e.message}"))

                trySendBlocking(Result.failure(e))
            }

            awaitClose {}
        }
    }
}