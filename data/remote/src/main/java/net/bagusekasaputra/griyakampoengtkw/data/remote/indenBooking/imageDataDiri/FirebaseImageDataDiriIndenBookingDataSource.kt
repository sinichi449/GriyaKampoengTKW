package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.imageDataDiri

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseImageDataDiriIndenBookingDataSource(
    storageReference: StorageReference
): RemoteImageDataDiriIndenBookingDataSource {

    private val imageRef = storageReference
        .child(FirebaseNodes.IMAGE_INDEN_BOOKING)
        .child(FirebaseNodes.IMAGE_DATA_DIRI)


    override suspend fun download(model: ImageDataDiriIndenBookingModel): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val destinationUri = Uri.parse(model.uriStr)

            imageRef.child(ImageDataDiriIndenBookingModel.getFilename(model.keyId))
                .getFile(destinationUri)
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(true), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        if (it.localizedMessage == FirebaseNodes.OBJECT_NOT_FOUND_MESSAGE) {
                            continuation.resume(Result.success(false), null)
                        } else {
                            continuation.resume(Result.failure(it), null)
                        }
                    }
                }
        }
    }

    override suspend fun isExist(keyId: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            imageRef.child(ImageDataDiriIndenBookingModel.getFilename(keyId))
                .downloadUrl
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(true), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        if (it.localizedMessage == FirebaseNodes.OBJECT_NOT_FOUND_MESSAGE) {
                            continuation.resume(Result.success(false), null)
                        } else {
                            continuation.resume(Result.failure(it), null)
                        }
                    }
                }
        }
    }
}