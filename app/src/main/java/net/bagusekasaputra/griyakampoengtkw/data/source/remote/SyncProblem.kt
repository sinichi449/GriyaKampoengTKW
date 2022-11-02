package net.bagusekasaputra.griyakampoengtkw.data.source.remote

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes

object SyncProblem {
    
    fun updateFirebaseTimeStamp(databaseReference: DatabaseReference) {
        val currentMillis = System.currentTimeMillis()
        databaseReference.child(GriyaNodes.timestamp).setValue(currentMillis)
    }
    
    fun isSynchronized(databaseReference: DatabaseReference, localTimeStamp: Long): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.timestamp)
                .get()
                .addOnSuccessListener {
                    val firebaseTimeStamp = it.getValue<Long>()
                    
                    if (localTimeStamp == firebaseTimeStamp) {
                        trySendBlocking(Result.success(true))
                    } else {
                        trySendBlocking(Result.success(false))
                    }
                }
                .addOnFailureListener { 
                    trySendBlocking(Result.failure(it))
                }
            
            awaitClose {}
        }
    }
}