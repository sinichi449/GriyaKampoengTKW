package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.imageDataDiri

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri.RemoteFotoIdentitasIndenBookingDataSource
import java.io.File
import kotlin.coroutines.resume

class FirebaseFotoIdentitasIndenBookingDataSource(
    storageReference: StorageReference,
    externalFileDir: File?
): RemoteFotoIdentitasIndenBookingDataSource {

    private val imageRef = storageReference.child(
        "${FirebaseNodes.IMAGE_INDEN_BOOKING}/${FirebaseNodes.IMAGE_DATA_DIRI}"
    )
    private val dstImgFile = File(externalFileDir, FirebaseNodes.IMAGE_INDEN_BOOKING)

    init {
        if (!dstImgFile.exists()) {
            dstImgFile.mkdir()
        }
    }

    override suspend fun get(keyId: String): Result<Uri?> {
        return suspendCancellableCoroutine { continuation ->
            val imgFileName = "${keyId}.png"
            var downloadDestination = File(dstImgFile, FirebaseNodes.IMAGE_DATA_DIRI)
            if (!downloadDestination.exists()) {
                downloadDestination.mkdir()
            }
            downloadDestination = File(downloadDestination, imgFileName)

            imageRef.child(imgFileName)
                .getFile(downloadDestination)
                .addOnCompleteListener {
                    Log.d("INDEN_BOOKING", "Image $imgFileName download complete!")

                    if (continuation.isActive) {
                        continuation.resume(Result.success(downloadDestination.toUri()))
                    }
                }
                .addOnFailureListener {
                    Log.d("INDEN_BOOKING", "Failed to download image Data Diri \"$keyId\": " +
                            "${it.javaClass.simpleName}:${it.message}")

                    if (continuation.isActive) {
                        continuation.resume(Result.success(null))
                    }
                }
        }
    }

    override suspend fun insert(keyId: String, uri: Uri): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            val filename = "${keyId}.png"

            imageRef.child(filename)
                .putFile(uri)
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it))
                    }
                }
        }
    }

    /**
     * Technically this would be the same as insert() method, as Firebase Storage automatically
     * overwrite any same filename. So, here it is.
     */
    override suspend fun update(keyId: String, newUri: Uri): Result<Nothing?> {
        return insert(keyId, newUri)
    }

    override suspend fun delete(keyId: String): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            val fileName = "${keyId}.png"

            imageRef.child(fileName)
                .delete()
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it))
                    }
                }
        }
    }

}