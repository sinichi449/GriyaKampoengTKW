package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.hargaRumah

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume

class FirebaseHargaRumahDataSource(
    databaseReference: DatabaseReference,
): RemoteHargaRumahIndenBookingDataSource {

    private val hargaRumahRef = { keyId: String ->
        databaseReference
            .child(FirebaseNodes.INDEN_BOOKING)
            .child(keyId)
            .child(FirebaseNodes.HARGA_RUMAH)
    }

    override suspend fun get(keyId: String): Result<HargaRumahModel?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firebaseModel = snapshot.getValue<HargaRumahFirebaseModel>()
                    val standardModel = firebaseModel?.toStandardModel(keyId)

                    if (continuation.isActive) {
                        continuation.resume(Result.success(standardModel))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        val exception = error.toException()
                        continuation.resume(Result.failure(exception))
                    }
                }
            }

            hargaRumahRef(keyId).addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun update(keyId: String, newModel: HargaRumahModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            val firebaseModel = newModel.toFirebaseModel()

            hargaRumahRef(keyId)
                .setValue(firebaseModel)
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

    private data class HargaRumahFirebaseModel(
        val harga: Long = 0L,
        val tambahLuasan: Long = 0L,
    )

    private fun HargaRumahFirebaseModel.toStandardModel(keyId: String): HargaRumahModel {
        return this.let {
            HargaRumahModel(
                harga = it.harga,
                tambahLuasan = it.tambahLuasan,
                keyId = keyId,
            )
        }
    }

    private fun HargaRumahModel.toFirebaseModel(): HargaRumahFirebaseModel {
        return this.let {
            HargaRumahFirebaseModel(
                harga = it.harga,
                tambahLuasan = it.tambahLuasan,
            )
        }
    }
}