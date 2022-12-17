package net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
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
    private val externalDirFile: File?
): RemoteImageDataDiriDataSource {

    private val imageDataDiriRef = storageReference.child(FirebaseNodes.IMAGE_DATA_DIRI)

    override suspend fun get(kavlingKode: String): ImageDataDiriModel? {
        return callbackFlow {

            val dstDir = File(externalDirFile, FirebaseNodes.IMAGE_DATA_DIRI)
            if (!dstDir.exists())
                dstDir.mkdir()

            val name = getFileName(kavlingKode)
            val file = File(dstDir, name)
            val downloadTask = imageDataDiriRef.child(name)
                .getFile(file)
                .addOnSuccessListener {
                    Log.d("DEBUG_ME", "StorageImage->get(): Downloading \"$name\" ${it.bytesTransferred.toMegaBytes()}/${it.totalByteCount.toMegaBytes()} MB ...")

                    val isComplete = it.bytesTransferred == it.totalByteCount
                    if (isComplete) {
                        trySendBlocking(
                            ImageDataDiriModel(
                                kavlingKode = kavlingKode,
                                imgUri = file.toUri().toString(),
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    Log.d("DEBUG_ME", "StorageImage->get(): Failed to download \"$name\" : ${it.message}")

                    trySendBlocking(null)
                }

            awaitClose {
                if (downloadTask.isComplete) {
                    Log.d("DEBUG_ME", "StorageImage->get(): Download \"$name\" completed.")
                } else {
                    downloadTask.cancel()
                    Log.d("DEBUG_ME", "StorageImage->get(): Download \"$name\" canceled because of closed connection.")
                }
            }
        }.first()
    }

    override suspend fun insert(imageDataDiriModel: ImageDataDiriModel) {
        val uri = Uri.parse(imageDataDiriModel.imgUri)
        imageDataDiriRef.putFile(uri)
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "StorageImage->insert(): Uploading \"${getFileName(imageDataDiriModel.kavlingKode)}\" ${it.bytesTransferred.toMegaBytes()}/${it.totalByteCount.toMegaBytes()} MB ...")
            }
            .addOnFailureListener {
                throw it
            }
    }

    override suspend fun update(
        oldModel: ImageDataDiriModel,
        newModel: ImageDataDiriModel
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(imageDataDiriModel: ImageDataDiriModel) {
        TODO("Not yet implemented")
    }

    private fun getFileName(kavlingKode: String) = "${kavlingKode}_data_diri.png"

    private fun Long.toMegaBytes() = if (this > 0L)
            BigDecimal(1024 * 1024).let { aMegabyte ->
                BigDecimal(this).divide(aMegabyte, 2, RoundingMode.HALF_UP)
            }.toDouble()
        else
            0.0
}