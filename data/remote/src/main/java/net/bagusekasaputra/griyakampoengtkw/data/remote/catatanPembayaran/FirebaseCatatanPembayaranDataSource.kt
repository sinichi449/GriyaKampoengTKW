package net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

class FirebaseCatatanPembayaranDataSource(
    private val databaseReference: DatabaseReference,
): RemoteCatatanPembayaranDataSource {

    private val catatanRef = databaseReference.child(FirebaseNodes.CATATAN_PEMBAYARAN)

    override suspend fun getCatatan(kavlingKode: String): Result<CatatanPembayaranModel?> {
        return callbackFlow<Result<CatatanPembayaranModel?>> {
            catatanRef
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val catatanModel = snapshot.getValue<CatatanPembayaranModel>()

                    trySendBlocking(Result.success(catatanModel))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun addCatatan(
        kavlingKode: String,
        catatanPembayaranModel: CatatanPembayaranModel
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            catatanRef
                .child(kavlingKode)
                .setValue(catatanPembayaranModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            catatanRef
                .child(kavlingKode)
                .removeValue()
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }
}