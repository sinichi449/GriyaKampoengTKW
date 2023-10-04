package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.ConnectionUtil.resumeIfActive
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File

class StorageFotoTambahanPembayaranDataSource(
    storageReference: StorageReference,
    private val externalFilesDir: File?,
): RemoteFotoTambahanPembayaranDataSource {

    private val ref = storageReference.child(FirebaseNodes.IMAGE_TAMBAHAN_PEMBAYARAN)

    init {
        // Create directory for foto tambahan pembayaran
        val dstDir = File(externalFilesDir, FotoTambahanPembayaranModel.DST_FOLDER)
        if (!dstDir.exists()) dstDir.mkdir()
    }

    override suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaranModel?> {
        return suspendCancellableCoroutine { continuation ->
            val model = FotoTambahanPembayaranModel(
                kavling = kavling,
                tambahanPembayaranId = id,
            )
            val filepath = model.getKavlingAndFilePath()
            val dstFile = File(File(externalFilesDir, FotoTambahanPembayaranModel.DST_FOLDER), filepath)

            FotoTambahanPembayaranModel.createKavlingFolderIfNotExist(externalFilesDir, kavling)

            ref.child(filepath).getFile(dstFile)
                .addOnCompleteListener {
                    if (dstFile.exists()) {
                        continuation.resumeIfActive(Result.success(
                            model.copy(uri = dstFile.toUri().toString())
                        ))
                    } else {
                        continuation.resumeIfActive(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    continuation.resumeIfActive(Result.failure(it))
                }
        }
    }

    override suspend fun insert(model: FotoTambahanPembayaranModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            val filepath = model.getKavlingAndFilePath()
            val targetUploadUri = "${FotoTambahanPembayaranModel.DST_FOLDER}/${filepath}".let {
                File(externalFilesDir, it).toUri()
            }

            ref.child(filepath)
                .putFile(targetUploadUri)
                .addOnCompleteListener {
                    continuation.resumeIfActive(Result.success(null))
                }
                .addOnFailureListener {
                    continuation.resumeIfActive(Result.failure(it))
                }
        }
    }

    override suspend fun isFotoExist(kavling: String, id: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val targetRef = FotoTambahanPembayaranModel(
                kavling = kavling,
                tambahanPembayaranId = id,
            ).let {
                ref.child(it.getKavlingAndFilePath())
            }

            targetRef.downloadUrl
                .addOnSuccessListener {
                    continuation.resumeIfActive(Result.success(true))
                }
                .addOnFailureListener {
                    if (it.message == "Object does not exist at location.") {
                        continuation.resumeIfActive(Result.success(false))
                    } else {
                        it.printStackTrace()
                        continuation.resumeIfActive(Result.failure(it))
                    }
                }
        }
    }
}