package net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.util.ConnectionUtil
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseKavlingRepository @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteKavlingRepository {

    private val kavlingRef = databaseReference.child(GriyaNodes.kavlings)

    override suspend fun getAllKavlings(blockKode: String): Result<List<KavlingModel>?> {
        return callbackFlow<Result<List<KavlingModel>?>> {

            val gotResult = AtomicBoolean(false)

            kavlingRef
                .child(blockKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    Log.d(LOG_TAG, "Getting kavlings from server success")
                    gotResult.set(true)

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
                    Log.d(LOG_TAG, "Getting kavlings fails: ${it.message}")
                    trySendBlocking(Result.failure(it))
                }

            ConnectionUtil.createRequestTimeout(
                gotResult = gotResult.get(),
                onTimeOut = {
                    Log.d(LOG_TAG, "Timeout reached")
                    trySendBlocking(Result.failure(UnknownError("Koneksi menuju server gagal, periksa koneksi Anda.")))
                },
                timeoutMillis = 3000
            )

            awaitClose {
                Log.d(LOG_TAG, "Getting kavlings connection closed.")
            }
        }.first()
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

    override fun editKavling(
        blockKode: String,
        oldKavling: KavlingModel,
        newKavling: KavlingModel
    ): Flow<Result<Boolean>> {
        Log.d(LOG_TAG, "Editting kavling ${oldKavling.kode}")
        return callbackFlow {
            isKavlingExists(blockKode, oldKavling).collect { exist ->
                if (exist) {
                    Log.d(LOG_TAG, "Found kavling ${oldKavling.kode} in database")
                    databaseReference
                        .child(GriyaNodes.kavlings)
                        .child(blockKode)
                        .child(oldKavling.kode)
                        .setValue(newKavling)
                        .addOnSuccessListener {
                            trySendBlocking(Result.success(true))
                        }
                        .addOnFailureListener {
                            trySendBlocking(Result.failure(it))
                        }
                } else {
                    Log.d(LOG_TAG, "Kavling ${oldKavling.kode} doesn't in database")
                    trySendBlocking(Result.success(false))
                }
            }

            awaitClose {  }
        }
    }

    override fun removeKavling(blockKode: String, kavlingKode: String): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.kavlings)
                .child(blockKode)
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

    private fun isKavlingExists(blockKode: String, kavling: KavlingModel): Flow<Boolean> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.kavlings)
                .child(blockKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.hasChild(kavling.kode)) {
                        trySendBlocking(true)
                    } else {
                        trySendBlocking(false)
                    }
                }

            awaitClose {}
        }
    }
}