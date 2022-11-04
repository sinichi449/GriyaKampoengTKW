package net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHargaKavlingSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteHargaKavlingSource {

    init {
        databaseReference.child(GriyaNodes.hargaKavling).keepSynced(true)
    }

    override fun getHargaKavlingModel(kavlingKode: String): Flow<Result<HargaKavlingModel?>> {
        Log.d(LOG_TAG, "Getting harga kavling $kavlingKode")
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.hargaKavling)
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val hargaKavlingModel = snapshot.getValue<HargaKavlingModel>()

                    trySendBlocking(Result.success(hargaKavlingModel))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Flow<Result<Boolean>> {
        Log.d(LOG_TAG, "Adding harga kavling on ${hargaKavlingModel.kavlingKode} with ${hargaKavlingModel.harga}")
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.hargaKavling)
                .child(hargaKavlingModel.kavlingKode)
                .setValue(hargaKavlingModel)
                .addOnSuccessListener {
                    Log.d(LOG_TAG, "Successfully adding harga kavling ${hargaKavlingModel.kavlingKode} -> ${hargaKavlingModel.harga}")
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

}