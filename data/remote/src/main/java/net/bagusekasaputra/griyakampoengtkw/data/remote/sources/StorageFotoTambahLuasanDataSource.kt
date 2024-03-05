package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahLuasanModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class StorageFotoTambahLuasanDataSource(
    private val storageReference: StorageReference,
    private val externalFilesDir: File?
): RemoteFotoTambahLuasanDataSource {

    private val ref = storageReference.child(FirebaseNodes.IMAGE_TAMBAH_LUASAN)

    init {
        // Create directory for foto if not available
        val dstDir = File(externalFilesDir, FotoPembayaranModel.DST_FOLDER)
        if (!dstDir.exists()) dstDir.mkdir()
    }

    override suspend fun get(kavling: String, id: String): Result<FotoTambahLuasanModel?> {
        return suspendCancellableCoroutine { continuation ->
            val model = FotoTambahLuasanModel(tambahLuasanId = id, kavling = kavling, uri = "")
            val dstFile = File(externalFilesDir, FotoTambahLuasanModel.DST_FOLDER).let {
                File(it, "$kavling/$id.png")
            }
            FotoTambahLuasanModel.createKavlingFolderIfNotExists(externalFilesDir, kavling)

            Log.d("DEBUG_ME", "Saving to ${dstFile.toUri()}")

            ref.child(model.getKavlingAndFilePath())
                .getFile(dstFile)
                .addOnProgressListener {}
                .addOnCompleteListener {
                    if (continuation.isActive) {
                        if (dstFile.exists()) {
                            val data = model.copy(uri = dstFile.toUri().toString())
                            continuation.resume(Result.success(data), null)
                        } else {
                            continuation.resume(Result.success(null), null)
                        }
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        if (it is StorageException) {
                            continuation.resume(Result.success(null), null)
                        } else {
                            continuation.resume(Result.failure(it), null)
                        }
                    }
                }
        }
    }

    override suspend fun insert(model: FotoTambahLuasanModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            val kavlingAndFilePath = model.getKavlingAndFilePath()
            val uri = "${FotoTambahLuasanModel.DST_FOLDER}/${kavlingAndFilePath}".let {
                File(externalFilesDir, it).toUri()
            }

            ref.child(kavlingAndFilePath)
                .putFile(uri)
                .addOnProgressListener {  }
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
}