package net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseIndenBookingCatatanPembayaranDataSource(
    databaseReference: DatabaseReference
): RemoteIndenBookingCatatanPembayaranDataSource {

    private val catatanPembayaranRef = { keyId: String ->
        lazy {
            databaseReference.child(FirebaseNodes.INDEN_BOOKING)
                .child(keyId)
                .child(FirebaseNodes.CATATAN_PEMBAYARAN)
        }
    }

    override suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaranModel?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val catatanPembayaran = snapshot.getValue<IndenBookingCatatanPembayaranModel>()

                        continuation.resume(Result.success(catatanPembayaran), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        val exception = error.toException()

                        continuation.resume(Result.failure(exception), null)
                    }
                }
            }

            catatanPembayaranRef(keyId).value.addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun insert(model: IndenBookingCatatanPembayaranModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            catatanPembayaranRef(model.keyId).value
                .setValue(model)
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