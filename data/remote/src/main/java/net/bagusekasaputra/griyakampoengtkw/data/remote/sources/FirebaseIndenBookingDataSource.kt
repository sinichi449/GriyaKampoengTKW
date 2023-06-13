package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
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

}