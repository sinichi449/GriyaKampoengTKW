package net.bagusekasaputra.griyakampoengtkw.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import java.util.concurrent.atomic.AtomicBoolean

object FirebaseRequestHelper {

    suspend fun <O> getOperation(
        pathToChild: DatabaseReference,
        onGetSnapshot: (snapshot: DataSnapshot) -> O?,
        timeOutMsg: String = "",
        onClosedConnection: () -> Unit,
    ): Result<O?> {
        return callbackFlow<Result<O?>> {
            val gotResult = AtomicBoolean(false)

            pathToChild.get()
                .addOnSuccessListener { snapshot ->
                    gotResult.set(true)

                    val resultObject = onGetSnapshot(snapshot)

                    trySendBlocking(Result.success(resultObject))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    trySendBlocking(Result.failure(it))
                }

            ConnectionUtil.createRequestTimeout(
                gotResult = gotResult.get(),
                onTimeOut = {
                    trySendBlocking(Result.failure(UnknownError(timeOutMsg)))
                }
            )

            awaitClose { onClosedConnection() }
        }.first()
    }

    // For crawling reports
    suspend fun <O> getOperationNoTimeout(
        pathToChild: DatabaseReference,
        onGetSnapshot: (snapshot: DataSnapshot) -> O?,
        onClosedConnection: () -> Unit,
    ): Result<O?> {
        return callbackFlow<Result<O?>> {
            pathToChild.get()
                .addOnSuccessListener { snapshot ->
                    val resultObject = onGetSnapshot(snapshot)

                    trySendBlocking(Result.success(resultObject))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose { onClosedConnection() }
        }.first()
    }

    // Not using time out
    suspend fun insertOperation(
        targetChild: DatabaseReference,
        valueToInsert: Any?,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {

            targetChild.setValue(valueToInsert)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }


            awaitClose {  }
        }.first()
    }

    /**
     * Notify the user will he/she want to overwrite the existing data.
     * This is, I think, important in the data sensitive context.
     */
    suspend fun insertOperationAlertOverwrite(
        targetChild: DatabaseReference,
        valueToInsert: Any?,
        existMsg: String,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val childExist = isDataExist(targetChild)

            if (childExist) {
                trySendBlocking(Result.failure(Exception(existMsg)))
            } else {
                targetChild.setValue(valueToInsert)
                    .addOnSuccessListener {
                        trySendBlocking(Result.success(null))
                    }
                    .addOnFailureListener {
                        trySendBlocking(Result.failure(it))
                    }
            }

            awaitClose { }
        }.first()
    }

    suspend fun updateOperation(
        targetChild: DatabaseReference,
        newValue: Any?,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // First, I remove the existing value.
            targetChild.removeValue()
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            // Then, add the new value
            targetChild.setValue(newValue)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    suspend fun updateOperationAndSetNewChild(
        toBeDeletedChild: DatabaseReference,
        newChild: DatabaseReference,
        newValue: Any?,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // First, I remove the existing value.
            toBeDeletedChild.removeValue()
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            // Then, add the new value
            newChild.setValue(newValue)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    suspend fun deleteOperation(targetChild: DatabaseReference): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            targetChild.removeValue()
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private suspend fun isDataExist(target: DatabaseReference): Boolean {
        return callbackFlow<Boolean> {
            target.get()
                .addOnSuccessListener { snapshot ->
                    val data = snapshot.getValue<PembayaranModel>()

                    // If null, then the data isn't exist. SAFE!
                    if (data == null)
                        trySendBlocking(false)
                    else
                        // If not null, then the data is indeed already exist. BEWARE!
                        trySendBlocking(true)
                }
            awaitClose {  }
        }.first()
    }
}