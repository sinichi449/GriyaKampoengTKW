package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseIndenBookingDataSource(
    databaseReference: DatabaseReference,
): RemoteIndenBookingDataSource {

    private val indenBookingRef = databaseReference.child(FirebaseNodes.INDEN_BOOKING)

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

    override suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hargaRumah = snapshot.getValue<HargaRumahModel>()

                    continuation.resume(Result.success(hargaRumah))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    continuation.resume(Result.failure(exception))
                }
            }

            indenBookingRef.child(keyId).child(FirebaseNodes.HARGA_RUMAH)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

}