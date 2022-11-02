package net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseKavlingRepository @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteKavlingRepository {

    init {
        databaseReference.child(GriyaNodes.kavlings).keepSynced(true)
    }

    override fun getAllKavlings(blockKode: String): Flow<Result<List<KavlingModel>>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.kavlings)
                .child(blockKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val hashMap = snapshot.getValue<HashMap<String, KavlingModel>>()
                    val kavlings = ArrayList<KavlingModel>()

                    hashMap?.keys?.forEach { key ->
                        hashMap[key]?.let { model ->
                            kavlings.add(model)
                        }
                    }

                    trySendBlocking(Result.success(kavlings))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun addKavling(blockKode: String, kavlingModel: KavlingModel): Flow<Result<Boolean>> {
        Log.d(LOG_TAG, "Adding new kavling in $blockKode which has a code ${kavlingModel.kode}")
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.kavlings)
                .child(blockKode)
                .child(kavlingModel.kode)
                .setValue(kavlingModel)
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