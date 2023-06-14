package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseBaselinePembayaranDataSource(
    databaseReference: DatabaseReference,
): RemoteBaselinePembayaranDataSource {

    private val baselinePembayaranRef by lazy {
        databaseReference.child(FirebaseNodes.BASELINE_PEMBAYARAN)
    }

    init {
        Log.d("FIREBASE_URL", "BaselinePembayaran is at $baselinePembayaranRef")
    }

    override suspend fun get(kavling: String): Result<BaselinePembayaranModel?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val baseline = snapshot.getValue<BaselinePembayaranModel>()

                        continuation.resume(Result.success(baseline), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        val exception = error.toException()
                        exception.printStackTrace()

                        continuation.resume(Result.failure(exception), null)
                    }
                }
            }

            baselinePembayaranRef.child(kavling)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = baselinePembayaranRef.child(model.kavling),
            valueToInsert = model,
        )
    }

}