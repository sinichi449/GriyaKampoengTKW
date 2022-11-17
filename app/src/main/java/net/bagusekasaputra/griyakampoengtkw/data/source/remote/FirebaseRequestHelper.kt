package net.bagusekasaputra.griyakampoengtkw.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.ConnectionUtil
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
}