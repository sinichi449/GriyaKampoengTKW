package net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.pembayaran

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.RemotePembayaranIndenBookingDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebasePembayaranIndenBookingDataSource(
    databaseReference: DatabaseReference
): RemotePembayaranIndenBookingDataSource {

    private val indenBookingRef = databaseReference.child(FirebaseNodes.INDEN_BOOKING)

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

            indenBookingRef.child(keyId).child(FirebaseNodes.FORM_PEMBAYARAN)
                .addListenerForSingleValueEvent(eventListener)
        }
    }
}