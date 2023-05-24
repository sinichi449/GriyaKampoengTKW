package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseIndenBookingDataSource(
    databaseReference: DatabaseReference,
    storageReference: StorageReference,
    externalFileDir: File?,
): RemoteIndenBookingDataSource {

    private val indenBookingRef = databaseReference.child(FirebaseNodes.INDEN_BOOKING)
    private val imageRef = storageReference.child(FirebaseNodes.IMAGE_INDEN_BOOKING)
    private val dstImgFile = File(externalFileDir, FirebaseNodes.IMAGE_INDEN_BOOKING)

    init {
        if (!dstImgFile.exists()) {
            dstImgFile.mkdir()
        }
    }

    override suspend fun getAllKeyIds(): Result<List<String>?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val keyIds = mutableListOf<String>()

                    snapshot.children.forEach { ds ->
                        val keyId = ds.key

                        if (!keyId.isNullOrEmpty()) {
                            keyIds.add(keyId)
                        }
                    }

                    continuation.resume(Result.success(keyIds))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    continuation.resume(Result.failure(exception))
                }
            }

            indenBookingRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun getDataDiri(keyId: String): Result<DataDiriModel?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataDiri = snapshot.getValue<DataDiriModel>()

                    continuation.resume(Result.success(dataDiri))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    continuation.resume(Result.failure(exception))
                }
            }

            indenBookingRef.child(keyId).child(FirebaseNodes.DATA_DIRI)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun getAllPembayaran(keyId: String): Result<List<PembayaranModel>?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pembayaranList = mutableListOf<PembayaranModel>()

                    val mapPembayaran = snapshot.getValue<HashMap<String, PembayaranModel>>()
                    mapPembayaran?.keys?.forEach { termin ->
                        val pembayaran = mapPembayaran[termin]

                        if (pembayaran != null) {
                            pembayaranList.add(pembayaran)
                        }
                    }

                    continuation.resume(Result.success(pembayaranList))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    continuation.resume(Result.failure(exception))
                }
            }

            indenBookingRef.child(keyId).child(FirebaseNodes.FORM_PEMBAYARAN)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun getFotoIdentitas(keyId: String): Result<Uri?> {
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