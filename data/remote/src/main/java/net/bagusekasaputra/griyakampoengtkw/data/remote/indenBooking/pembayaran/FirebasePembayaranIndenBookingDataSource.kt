package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.pembayaran

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.RemotePembayaranIndenBookingDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebasePembayaranIndenBookingDataSource(
    databaseReference: DatabaseReference
): RemotePembayaranIndenBookingDataSource {

    private val pembayaranRef = { keyId: String ->
        databaseReference
            .child(FirebaseNodes.INDEN_BOOKING)
            .child(keyId)
            .child(FirebaseNodes.FORM_PEMBAYARAN)
    }

    override suspend fun getAll(keyId: String): Result<List<PembayaranModel>?> {
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

            pembayaranRef(keyId).addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun insert(keyId: String, model: PembayaranModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            pembayaranRef(keyId).child(model.termin)
                .setValue(model)
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
}