package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

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
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File

class StorageFotoPembayaranDataSource(
    private val storageReference: StorageReference,
    private val externalFilesDir: File?,
): RemoteFotoPembayaranDataSource {

    private val fotoPembayaranRef = storageReference.child(FirebaseNodes.IMAGES_FOTO_PEMBAYARAN)

    private fun referenceFromUrl(pembayaranImageRef: StorageReference, model: FotoPembayaranModel): StorageReference {
        val url = buildString {
            append("${pembayaranImageRef}/")
            append(model.getKavlingAndFilePath())
        }
        return storageReference.storage.getReferenceFromUrl(url)
    }

    init {
        // Create directory for foto pembayaran
        val dstDir = File(externalFilesDir, FotoPembayaranModel.DST_FOLDER)
        if (!dstDir.exists()) dstDir.mkdir()
    }



    override suspend fun get(kavlingKode: String, termin: String): FotoPembayaranModel? {
        return callbackFlow {
            val model = FotoPembayaranModel(kavlingKode = kavlingKode, termin = termin)
            val filename = model.getKavlingAndFilePath()
            val dstFile = File(externalFilesDir, FotoPembayaranModel.DST_FOLDER).let { rootDir ->
                File(rootDir, filename)
            }
            FotoPembayaranModel.createKavlingFolderIfNotExist(externalFilesDir, kavlingKode)

            Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Saving \"$filename\" to ${dstFile.toUri()}")

            val onProgressListener = OnProgressListener<FileDownloadTask.TaskSnapshot> {
                Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Downloading \"$filename\" is ${it.bytesTransferred}/${it.totalByteCount} bytes ...")
            }
            val onCompleteListener = OnCompleteListener<FileDownloadTask.TaskSnapshot> {
                Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Download \"$filename\" is completed!")
                if (dstFile.exists()) {
                    trySendBlocking(
                        FotoPembayaranModel(
                            kavlingKode = kavlingKode,
                            termin = termin,
                            uriStr = dstFile.toUri().toString(),
                        )
                    )
                } else {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Resulting downloaded file \"$filename\" is not found! Sending null instead.")
                    trySendBlocking(null)
                }
            }
            val onFailureListener = OnFailureListener {
                it.printStackTrace()

                Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Failed to download \"$filename\": ${it.message}")
                trySendBlocking(null)
            }

            val terminReference = fotoPembayaranRef.child(model.getKavlingAndFilePath())
            Log.d("FOTO_PEMBAYARAN", "Reference is $terminReference")
            val downloadFotoPembayaranTask = terminReference.getFile(dstFile)

            downloadFotoPembayaranTask
                .addOnProgressListener(onProgressListener)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener)

            awaitClose {
                Log.d("DEBUG_ME", "StorageFotoPembayaran->get(): Connection to storage GET \"$filename\" is closed. Detaching listeners ...")

                downloadFotoPembayaranTask
                    .removeOnProgressListener(onProgressListener)
                    .removeOnCompleteListener(onCompleteListener)
                    .removeOnFailureListener(onFailureListener)
            }
        }.first()
    }

    override fun insert(newModel: FotoPembayaranModel): Flow<Result<Nothing?>> {
        return callbackFlow {
            val kavlingAndFilePath = newModel.getKavlingAndFilePath()
            val uri = "${FotoPembayaranModel.DST_FOLDER}/${kavlingAndFilePath}".let { fullpath ->
                File(externalFilesDir, fullpath)
                    .toUri()
            }

            Log.d("DEBUG_ME", "StorageFotoPembayaran->insert(): Prepare to upload $uri ...")

            fotoPembayaranRef.child(kavlingAndFilePath)
                .putFile(uri)
                .addOnProgressListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->insert(): Uploading \"$kavlingAndFilePath\" is ${it.bytesTransferred} / ${it.totalByteCount} bytes ...")
                }
                .addOnCompleteListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->insert(): Completed uploading a file \"$kavlingAndFilePath\" to remote server!!")
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->insert(): Error uploading a file \"$kavlingAndFilePath\" : ${it.message}")
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {
                Log.d("DEBUG_ME","StorageFotoPembayaran->insert(): Connection for upload \"$kavlingAndFilePath\" is closed.")
            }
        }
    }

    override suspend fun delete(kavlingKode: String, termin: String): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val filename = FotoPembayaranModel(kavlingKode = kavlingKode, termin = termin).let { model ->
                model.getKavlingAndFilePath()
            }
            Log.d("DEBUG_ME", "StorageFotoPembayaran->delete(): Attempting to delete $filename ...")
            fotoPembayaranRef.child(filename)
                .delete()
                .addOnCompleteListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->delete(): Success deleting $filename from remote data source!!")
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->delete(): Failed to delete $filename : ${it.message}")
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>> {
        return callbackFlow {
            val imageRef = FotoPembayaranModel(kavlingKode = kavlingKode, termin = termin).let {
                fotoPembayaranRef.child(it.getKavlingAndFilePath())
            }
            imageRef.downloadUrl
                .addOnSuccessListener {
                    Log.d("DEBUG_ME", "StorageFotoPembayaran->isFotoPembayaranExist(): Found foto pembayaran $kavlingKode on termin $termin !!")
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    if (it.message == "Object does not exist at location.") {
                        Log.d("DEBUG_ME", "StorageFotoPembayaran->isFotoPembayaranExist(): NOT Found foto pembayaran $kavlingKode on $termin.")
                        trySendBlocking(Result.success(false))
                    } else {
                        Log.d("DEBUG_ME",
                            "StorageFotoPembayaran->isFotoPembayaranExist(): Error at getting download url for foto pembayaran $kavlingKode on termin $termin : ${it.message}")
                        it.printStackTrace()

                        trySendBlocking(Result.failure(it))
                    }
                }

            awaitClose {  }
        }
    }

    override suspend fun deleteAll(kavlingKode: String): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            fotoPembayaranRef.child(kavlingKode)
                .listAll()
                .addOnCompleteListener {
                    it.result.items.forEach { fotoPembayaran ->
                        fotoPembayaran
                            .delete()
                            .addOnCompleteListener {
                                Log.d("DEBUG_ME", "StorageFotoPembayaran->deleteAll(): Success deleting ${fotoPembayaran.path} ...")
                            }
                            .addOnFailureListener { exception ->
                                exception.printStackTrace()

                                Log.d("DEBUG_ME", "StorageFotoPembayaran->deleteAll(): FAILED to delete ${fotoPembayaran.path} in $kavlingKode : ${exception.message}")
                            }
                    }

                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    Log.d("DEBUG_ME", "StorageFotoPembayaran->deleteAll(): FAILED to list Foto Pembayaran files in $kavlingKode : ${it.message}")

                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }
}