package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseFeeMarketingDataSource(
    private val databaseReference: DatabaseReference,
): RemoteFeeMarketingDataSource {

    private val feeMarketingRef = databaseReference.child(FirebaseNodes.FEE_MARKETING)

    init {
        Log.d("FIREBASE_URL", "Fee Marketing is at $feeMarketingRef")
    }

    override suspend fun getByKavlingKode(kavlingKode: String): Result<FeeMarketingModel?> {
        return callbackFlow<Result<FeeMarketingModel?>> {
            feeMarketingRef
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val feeMarketingModel = snapshot.getValue<FeeMarketingModel>()

                    trySendBlocking(Result.success(feeMarketingModel))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun getFromBackup(
        backupName: String,
        kavlingKode: String
    ): Result<FeeMarketingModel?> {
        return suspendCoroutine { continuation ->
            val backupFeeMarketingRef = FirebaseNodes
                .getBackupNode(databaseReference, backupName, FirebaseNodes.FEE_MARKETING)
                .child(kavlingKode)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val feeMarketing = snapshot.getValue<FeeMarketingModel>()

                    continuation.resume(Result.success(feeMarketing))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            }

            backupFeeMarketingRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun addFeeMarketing(
        kavlingKode: String,
        feeMarketingModel: FeeMarketingModel
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            feeMarketingRef
                .child(kavlingKode)
                .setValue(feeMarketingModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun updateFeeMarketing(
        kavlingKode: String,
        oldFeeMarketingModel: FeeMarketingModel,
        newFeeMarketingModel: FeeMarketingModel
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // First we remove the old value
            feeMarketingRef
                .child(kavlingKode)
                .removeValue()
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            // Then we add the new value
            feeMarketingRef
                .child(kavlingKode)
                .setValue(newFeeMarketingModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }


            awaitClose {  }
        }.first()
    }

    override suspend fun deleteFeeMarketing(kavlingKode: String): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            feeMarketingRef
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