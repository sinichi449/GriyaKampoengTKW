package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseIndenBookingAmbilKuitansiDataSource(
    databaseReference: DatabaseReference
): RemoteIndenBookingAmbilKuitansiDataSource {

    private val ambilKuitansiRef = { keyId: String ->
        lazy {
            databaseReference.child(FirebaseNodes.INDEN_BOOKING)
                .child(keyId)
                .child(FirebaseNodes.AMBIL_KUITANSI)
        }
    }

    override suspend fun get(
        keyId: String,
        termin: String
    ): Result<IndenBookingAmbilKuitansiModel?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sudahAmbil = snapshot.getValue<Boolean>() ?: false

                    if (continuation.isActive) {
                        val ambilKuitansi = IndenBookingAmbilKuitansiModel(keyId, termin, sudahAmbil)
                        continuation.resume(Result.success(ambilKuitansi), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        val exception = error.toException()
                        continuation.resume(Result.failure(exception), null)
                    }
                }
            }

            ambilKuitansiRef(keyId).value.child(termin).addListenerForSingleValueEvent(eventListener)
        }
    }
}