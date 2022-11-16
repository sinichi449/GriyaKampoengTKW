package net.bagusekasaputra.griyakampoengtkw.data.source.remote.feeMarketing

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFeeMarketingDataSource @Inject constructor(
    private val databaseReference: DatabaseReference,
): RemoteFeeMarketingDataSource {

    private val feeMarketingRef = databaseReference.child(GriyaNodes.feeMarketing)

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