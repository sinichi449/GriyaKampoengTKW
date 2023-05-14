@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking

import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.remote.NotificationUtil
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

class FirebaseIndenBookingDataSource(
    databaseReference: DatabaseReference,
    storageReference: StorageReference,
    private val externalFilesDir: File?,
    private val localBroadcast: LocalBroadcastManager,
): RemoteIndenBookingDataSource {

    private val indenBookingRef = databaseReference.child(FirebaseNodes.INDEN_BOOKING)
    private val fotoIndenBookingRef = storageReference.child(FirebaseNodes.IMAGE_INDEN_BOOKING)

    override suspend fun getAll(): Result<List<IndenBookingModel>?> {
        return callbackFlow<Result<List<IndenBookingModel>?>> {
            indenBookingRef.get()
                .addOnSuccessListener { snapshot ->
                    val timeMillisAndIndenFirebase = snapshot.getValue<HashMap<String, IndenBookingFirebaseModel>>()

                    if (timeMillisAndIndenFirebase != null) {
                        val listIndenBooking = mutableListOf<IndenBookingModel>()

                        timeMillisAndIndenFirebase.keys.forEach { timeMillis ->
                            timeMillisAndIndenFirebase[timeMillis]?.also {
                                listIndenBooking.add(it.toModel())
                            }
                        }

                        trySendBlocking(Result.success(listIndenBooking))
                    } else {
                        trySendBlocking(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun insert(model: IndenBookingModel): Result<Nothing?> {
        val uploadFotoResult = uploadFotoPembayaranIndenBooking(model)

        return if (uploadFotoResult.isSuccess) {
            FirebaseRequestHelper.insertOperation(
                targetChild = indenBookingRef.child(model.timeMillis.toString()),
                valueToInsert = model.toFirebaseModel(),
            )
        } else {
            val uploadErrorCause = uploadFotoResult.exceptionOrNull()
            uploadErrorCause?.printStackTrace()

            Result.failure(uploadErrorCause ?: Throwable("UNKNOWN Error mengupload foto kuitansi Inden Booking"))
        }
    }

    override suspend fun getFotoPembayaranPath(model: IndenBookingModel): Result<String?> {
        return callbackFlow<Result<String?>> {
            IndenBookingModel.createStorageFolderIfNotExist(externalFilesDir)

            val fileName = model.getFileName()
            val dstFile = File(externalFilesDir, model.getStorageFolderAndFileName())

            printLog("downloadFotoPembayaranIndenBooking", "Prepare saving \"$fileName\" to ${dstFile.absolutePath}")

            fotoIndenBookingRef.child(fileName)
                .getFile(dstFile)
                .addOnProgressListener {
                    val progress = BigDecimal(it.bytesTransferred)
                        .divide(BigDecimal(it.totalByteCount), 2, RoundingMode.CEILING)
                        .multiply(BigDecimal(100))
                        .toInt()
                    val intent = Intent(NotificationUtil.INTENT_ACTION).apply {
                        putExtra(NotificationUtil.EXTRAS_TITLE, "Mengunduh \"$fileName\" ...")
                        putExtra(NotificationUtil.EXTRAS_PROGRESS, progress)
                        putExtra(NotificationUtil.EXTRAS_IS_COMPLETE, false)
                    }
                    localBroadcast.sendBroadcast(intent)

                    printLog("downloadFotoPembayaranIndenBooking", "Downloading \"$fileName\" is ${it.bytesTransferred}/${it.totalByteCount} bytes ...")
                }
                .addOnCompleteListener {
                    val intent = Intent(NotificationUtil.INTENT_ACTION).apply {
                        putExtra(NotificationUtil.EXTRAS_TITLE, "Selesai mengunduh")
                        putExtra(NotificationUtil.EXTRAS_IS_COMPLETE, true)
                        putExtra(NotificationUtil.EXTRAS_TEXT_ON_COMPLETE, "\"$fileName\"")
                    }
                    localBroadcast.sendBroadcast(intent)

                    printLog("downloadFotoPembayaranIndenBooking", "Completed download \"$fileName\" !")
                    if (dstFile.exists()) {
                        trySendBlocking(Result.success(dstFile.absolutePath))
                    } else {
                        printLog("", "Resulting downloaded file \"$fileName\" is not found!")

                        trySendBlocking(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    trySendBlocking(Result.failure(it))

                    printLog("downloadFotoPembayaranIndenBooking", "Failed to download \"$fileName\" : ${it.message}")
                }

            awaitClose {}
        }.first()
    }

    override suspend fun deleteFotoPembayaran(model: IndenBookingModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            fotoIndenBookingRef.child(model.getFileName())
                .delete()
                .addOnSuccessListener {
                    deleteFoto(model)

                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun delete(model: IndenBookingModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            indenBookingRef.child(model.timeMillis.toString())
                .removeValue()
                .addOnCompleteListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private suspend fun uploadFotoPembayaranIndenBooking(model: IndenBookingModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val localUri = File(model.fotoPembayaranPath).toUri()
            val fileName = model.getFileName()

            printLog("uploadFotoPembayaran", "Prepare to upload $localUri ...")

            fotoIndenBookingRef.child(fileName)
                .putFile(localUri)
                .addOnProgressListener {
                    val progress = BigDecimal(it.bytesTransferred)
                        .divide(BigDecimal(it.totalByteCount), 2, RoundingMode.CEILING)
                        .multiply(BigDecimal(100))
                        .toInt()
                    val intent = Intent(NotificationUtil.INTENT_ACTION).apply {
                        putExtra(NotificationUtil.EXTRAS_TITLE, "Mengupload \"$fileName\" ...")
                        putExtra(NotificationUtil.EXTRAS_PROGRESS, progress)
                        putExtra(NotificationUtil.EXTRAS_IS_COMPLETE, false)
                    }
                    localBroadcast.sendBroadcast(intent)

                    printLog("uploadFotoPembayaran", "Uploading \"$fileName\" is $progress%")
                }
                .addOnCompleteListener {
                    printLog("uploadFotoPembayaran", "Completed uploading \"$fileName\" into Remmote Server!")

                    cleanAndMoveImagePostUpload(model)

                    val intent = Intent(NotificationUtil.INTENT_ACTION).apply {
                        putExtra(NotificationUtil.EXTRAS_TITLE, "Berhasil mengupload")
                        putExtra(NotificationUtil.EXTRAS_IS_COMPLETE, true)
                        putExtra(NotificationUtil.EXTRAS_TEXT_ON_COMPLETE, "\"$fileName\"")
                    }
                    localBroadcast.sendBroadcast(intent)

                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    printLog("uploadFotoPembayaran", "Error uploading \"$fileName\" : ${it.message}")
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {
                printLog("uploadFotoPembayaran", "Connection for upload Inden Booking has been closed!")
            }
        }.first()
    }

    private fun cleanAndMoveImagePostUpload(model: IndenBookingModel) {
        val file = File(model.fotoPembayaranPath)

        // Move file
        val dstTargetMove = File(externalFilesDir, model.getStorageFolderAndFileName())
        file.renameTo(dstTargetMove)
    }

    private fun deleteFoto(model: IndenBookingModel) {
        model.getFileFotoPembayaran(externalFilesDir)
            .delete()
    }

    private fun printLog(methodName: String, message: String) {
        Log.d("DEBUG_ME", "FirebaseIndenBookingDataSource::$methodName(): $message")
    }

    data class IndenBookingFirebaseModel(
        val timeMillis: Long = 0L,
        val namaCostumer: String = "",
        val tanggalDibayar: String = "",
        val jumlahUang: Long = 0L,
        val noHp: String = "",
        val keterangan: String = "",
    ) {

        fun toModel(): IndenBookingModel {
            return IndenBookingModel(
                timeMillis = timeMillis,
                namaCostumer = namaCostumer,
                tanggalDibayar = tanggalDibayar,
                fotoPembayaranPath = "", // Let it filled by LocalDataSource...
                jumlahUang = jumlahUang,
                noHp = noHp,
                keterangan = keterangan,
            )
        }
    }

    private fun IndenBookingModel.toFirebaseModel(): IndenBookingFirebaseModel {
        return this.let {
            IndenBookingFirebaseModel(
                timeMillis = it.timeMillis,
                namaCostumer = it.namaCostumer,
                tanggalDibayar = it.tanggalDibayar,
                jumlahUang = it.jumlahUang,
                noHp = it.noHp,
                keterangan = it.keterangan,
            )
        }
    }
}