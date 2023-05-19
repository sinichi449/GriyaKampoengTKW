package net.bagusekasaputra.griyakampoengtkw.data.remote.statusPembayaran

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStatusPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.StatusPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseStatusPembayaranDataSource(
    databaseReference: DatabaseReference
): RemoteStatusPembayaranDataSource {

    private val statusPembayaranRef = databaseReference.child(FirebaseNodes.STATUS_PEMBAYARAN)

    override suspend fun get(kavling: String): Result<StatusPembayaranModel?> {
        return suspendCoroutine { continuation ->
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val statusPembayaranModel = snapshot.getValue<StatusPembayaranModel>()

                    continuation.resume(Result.success(statusPembayaranModel))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()

                    exception.printStackTrace()

                    continuation.resume(Result.failure(exception))
                }
            }

            statusPembayaranRef.child(kavling)
                .addListenerForSingleValueEvent(valueEventListener)
        }
    }

    override suspend fun insert(model: StatusPembayaranModel): Result<Nothing?> {
        return suspendCoroutine { continuation ->
            statusPembayaranRef.child(model.kavling)
                .setValue(model)
                .addOnSuccessListener {
                    continuation.resume(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    continuation.resume(Result.failure(it))
                }
        }
    }
}