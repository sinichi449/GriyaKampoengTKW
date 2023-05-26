package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.imageDataDiri

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri.RemoteFotoIdentitasIndenBookingDataSource
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseFotoIdentitasIndenBookingDataSource(
    storageReference: StorageReference,
    externalFileDir: File?
): RemoteFotoIdentitasIndenBookingDataSource {

    private val imageRef = storageReference.child(FirebaseNodes.IMAGE_INDEN_BOOKING)
    private val dstImgFile = File(externalFileDir, FirebaseNodes.IMAGE_INDEN_BOOKING)

    init {
        if (!dstImgFile.exists()) {
            dstImgFile.mkdir()
        }
    }

    override suspend fun get(keyId: String): Result<Uri?> {
        return suspendCoroutine { continuation ->
            val imgFileName = "${keyId}.png"
            var downloadDestination = File(dstImgFile, FirebaseNodes.IMAGE_DATA_DIRI)
            if (!downloadDestination.exists()) {
                downloadDestination.mkdir()
            }
            downloadDestination = File(downloadDestination, imgFileName)

            imageRef.child(FirebaseNodes.IMAGE_DATA_DIRI).child(imgFileName)
                .getFile(downloadDestination)
                .addOnProgressListener {
                    val bytesDownloaded = it.bytesTransferred
                    val totalBytes = it.totalByteCount

                    Log.d("INDEN_BOOKING", "Downloading image Data Diri: " +
                            "${bytesDownloaded}/${totalBytes} ...")
                }
                .addOnCompleteListener {
                    Log.d("INDEN_BOOKING", "Image $imgFileName download complete!")

                    continuation.resume(Result.success(downloadDestination.toUri()))
                }
                .addOnFailureListener {
                    Log.d("INDEN_BOOKING", "Failed to download image Data Diri \"$keyId\": " +
                            "${it.javaClass.simpleName}:${it.message}")

                    continuation.resume(Result.failure(it))
                }
        }
    }

}