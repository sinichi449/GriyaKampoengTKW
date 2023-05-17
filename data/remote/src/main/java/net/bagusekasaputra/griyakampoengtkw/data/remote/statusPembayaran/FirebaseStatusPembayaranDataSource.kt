package net.bagusekasaputra.griyakampoengtkw.data.remote.statusPembayaran

import com.google.firebase.database.DatabaseReference
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
        TODO("Not yet implemented")
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