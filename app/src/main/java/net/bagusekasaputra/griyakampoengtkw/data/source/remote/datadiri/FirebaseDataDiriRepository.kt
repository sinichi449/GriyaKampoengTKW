package net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataDiriRepository @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteDataDiriRepository {

    init {
        databaseReference.child(GriyaNodes.dataDiri).keepSynced(true)
    }

    override fun getDataDiri(kavlingKode: String): Flow<Result<DataDiriModel?>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.dataDiri)
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val model = snapshot.getValue<DataDiriModel>()
                    trySendBlocking(Result.success(model))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun addDataDiri(
        kavlingKode: String,
        dataDiriModel: DataDiriModel,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.dataDiri)
                .child(kavlingKode)
                .setValue(dataDiriModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose { }
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.dataDiri)
                .child(kavlingKode)
                .removeValue()
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

}