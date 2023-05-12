package net.bagusekasaputra.griyakampoengtkw.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.interfaces.remote.InitRemote
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class InitRemoteImpl(
    private val databaseReference: DatabaseReference
): InitRemote {

    override suspend fun checkMaintenance(): Result<Boolean> {
        return suspendCoroutine { continuation ->
            val maintenanceRef = databaseReference.child(FirebaseNodes.MAINTENTANCE)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val statusServer = snapshot.getValue<Boolean>()

                    if (statusServer != null) {
                        continuation.resume(Result.success(statusServer))
                    } else {
                        continuation.resumeWithException(Exception("Respon Maintenance Server === NULL !"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            }

            maintenanceRef.addListenerForSingleValueEvent(eventListener)
        }
    }
}