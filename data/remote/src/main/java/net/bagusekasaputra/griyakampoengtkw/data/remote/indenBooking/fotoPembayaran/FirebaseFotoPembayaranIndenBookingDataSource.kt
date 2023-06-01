package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.fotoPembayaran

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFotoPembayaranIndenBookingDataSource(
    storageReference: StorageReference
): RemoteFotoPembayaranIndenBookingDataSource {

    private val fotoPembayaranRef = { keyId: String ->
        storageReference
            .child(FirebaseNodes.IMAGE_INDEN_BOOKING)
            .child(FirebaseNodes.IMAGES_FOTO_PEMBAYARAN)
            .child(keyId)
    }


    override suspend fun download(model: FotoPembayaranIndenBookingModel): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val destinationUri = Uri.parse(model.uriStr)
            val filename = FotoPembayaranIndenBookingModel.getFilename(model.keyId, model.termin)

            fotoPembayaranRef(model.keyId).child(filename)
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

    override suspend fun insert(model: FotoPembayaranIndenBookingModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            fotoPembayaranRef(model.keyId)
                .child(FotoPembayaranIndenBookingModel.getFilename(model.keyId, model.termin))
                .putFile(Uri.parse(model.uriStr))
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(null), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it), null)
                    }
                }
        }
    }

    override suspend fun delete(keyId: String, termin: String): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            fotoPembayaranRef(keyId)
                .child(FotoPembayaranIndenBookingModel.getFilename(keyId, termin))
                .delete()
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(null), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it), null)
                    }
                }
        }
    }

    override suspend fun isExist(keyId: String, termin: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val filename = FotoPembayaranIndenBookingModel.getFilename(keyId, termin)

            fotoPembayaranRef(keyId).child(filename)
                .downloadUrl
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(true), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        if (it.localizedMessage == FirebaseNodes.OBJECT_NOT_FOUND_MESSAGE) {
                            Log.d("INDEN_BOOKING", "Foto Pembayaran \"$filename\" NOT FOUND!")

                            continuation.resume(Result.success(false), null)
                        } else {
                            continuation.resume(Result.failure(it), null)
                        }
                    }
                }
        }
    }
}