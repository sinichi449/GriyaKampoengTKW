package net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.OnProgressListener
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
    private val imageIndenBooking: RemoteFotoIdentitasIndenBookingDataSource,
): RemoteImageDataDiriDataSource {

    private val imageDataDiriRef = storageReference.child(FirebaseNodes.IMAGE_DATA_DIRI)

    override suspend fun get(kavlingKode: String): ImageDataDiriModel? {
        return callbackFlow {
            val dstDir = File(externalFilesDir, ImageDataDiriModel.DST_FOLDER)

            val name = getFileName(kavlingKode)
            val file = File(dstDir, name)

            Log.d("DEBUG_ME", "StorageImageDataDiri->get(): Saving \"$name\" to ${file.toUri()}")

            val progressListener = OnProgressListener<FileDownloadTask.TaskSnapshot> {
                Log.d("DEBUG_ME", "StorageImage->get(): Downloading \"$name\" ${it.bytesTransferred.toMegaBytes()}/${it.totalByteCount.toMegaBytes()} MB ...")
            }
            val onCompleteListener = OnCompleteListener<FileDownloadTask.TaskSnapshot> {
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
            val onFailureListener = OnFailureListener {
                it.printStackTrace()
                Log.d("DEBUG_ME", "StorageImage->get(): Failed to download \"$name\" : ${it.message}")

                trySendBlocking(null)
            }
            val downloadImageDataDiriTask = imageDataDiriRef.child(name).getFile(file)

            downloadImageDataDiriTask
                .addOnProgressListener(progressListener)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener)


            awaitClose {
                Log.d("DEBUG_ME", "StorageImage->get(): Connection to storage GET \"$name\" is closed. Detaching listeners ...")

                downloadImageDataDiriTask
                    .removeOnProgressListener(progressListener)
                    .removeOnCompleteListener(onCompleteListener)
                    .removeOnFailureListener(onFailureListener)
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


    /**
     * Inden Booking related
     */
    override suspend fun getFromIndenBooking(keyId: String): Result<Uri?> {
        return imageIndenBooking.get(keyId)
    }

    override suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?> {
        return imageIndenBooking.insert(keyId, uri)
    }

    override suspend fun updateFromIndenBooking(keyId: String, newUri: Uri): Result<Nothing?> {
        return imageIndenBooking.update(keyId, newUri)
    }

    override suspend fun deleteFromIndenBooking(keyId: String): Result<Nothing?> {
        return imageIndenBooking.delete(keyId)
    }




    private fun getFileName(kavlingKode: String) = "${kavlingKode}_data_diri.png"

    private fun Long.toMegaBytes() = if (this > 0L)
            BigDecimal(1024 * 1024).let { aMegabyte ->
                BigDecimal(this).divide(aMegabyte, 2, RoundingMode.HALF_UP)
            }.toDouble()
        else
            0.0
}

/**
 * This interface will prevent dependency to Inden Booking counterpart, since it is very unstable,
 * and instead inverting that relation.
 *
 * (Dependency Inversion?)
 */
interface RemoteFotoIdentitasIndenBookingDataSource {

    suspend fun get(keyId: String): Result<Uri?>

    suspend fun insert(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun update(keyId: String, newUri: Uri): Result<Nothing?>

    suspend fun delete(keyId: String): Result<Nothing?>

}