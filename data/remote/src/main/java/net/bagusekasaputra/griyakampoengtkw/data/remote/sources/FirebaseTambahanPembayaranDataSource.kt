package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.TambahanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseTambahanPembayaranDataSource(
    databaseReference: DatabaseReference,
): RemoteTambahanPembayaranDataSource {

    private val tambahanPembayaranRef = databaseReference.child(FirebaseNodes.TAMBAHAN_PEMBAYARAN)

    init {
        Log.d("FIREBASE_URL", "Tambahan Pembayaran is at $tambahanPembayaranRef")
    }

    override suspend fun getAll(kavling: String): Result<List<TambahanPembayaranModel>?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val idHashMap = snapshot.getValue<HashMap<String, TambahanPembayaranModel>>()

                    if (idHashMap != null) {
                        val tambahanPembayarans = buildList {
                            idHashMap.keys.forEach { key ->
                                idHashMap[key]?.also { model ->
                                    add(model)
                                }
                            }
                        }
                        // Prevent empty List
                        if (tambahanPembayarans.isNotEmpty()) {
                            continuation.resume(Result.success(tambahanPembayarans), null)
                        } else {
                            continuation.resume(Result.success(null), null)
                        }
                    } else {
                        continuation.resume(Result.success(null), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        continuation.resume(
                            Result.failure(error.toException()),
                            null,
                        )
                    }
                }
            }

            tambahanPembayaranRef.child(kavling)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun getById(kavling: String, id: String): Result<TambahanPembayaranModel?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue<TambahanPembayaranModel?>()

                    continuation.resume(Result.success(model), null)
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        continuation.resume(
                            Result.failure(error.toException()),
                            null,
                        )
                    }
                }

            }

            tambahanPembayaranRef
                .child(kavling)
                .child(id)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun insert(model: TambahanPembayaranModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            tambahanPembayaranRef
                .child(model.kavling)
                .child(model.id)
                .setValue(model)
                .addOnSuccessListener {
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

    override suspend fun update(
        kavling: String,
        id: String,
        newData: TambahanPembayaranModel
    ): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            // TODO: Check if is Exists
            tambahanPembayaranRef
                .child(kavling)
                .child(id)
                .setValue(newData)
                .addOnSuccessListener {
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

    override suspend fun delete(kavling: String, id: String): Result<Nothing?> {
        return Result.failure(NotImplementedError("An operation is not implemented: Not yet implemented"))
    }
}