package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

class FirebaseBiayaLainDataSource(
    databaseReference: DatabaseReference
): RemoteBiayaLainDataSource {

    private val biayaLainRef = databaseReference.child(FirebaseNodes.BIAYA_LAIN)

    init {
        Log.d("FIREBASE_URL", "Biaya Lain is at $biayaLainRef")
    }

    override fun getAll(): Flow<Result<List<BiayaLainModel>?>> {
        return callbackFlow {
            Log.d("FIREBASE_URL", "Biaya Lain is at $biayaLainRef")
            val valueListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val biayaLainMap = snapshot.getValue<HashMap<String, BiayaLainModel>>()

                    val listBiayaLain = mutableListOf<BiayaLainModel>()
                    biayaLainMap?.keys?.forEach { key ->
                        biayaLainMap[key]?.let { model ->
                            listBiayaLain.add(model)
                        }
                    }

                    trySendBlocking(Result.success(listBiayaLain))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(Result.failure(error.toException()))
                }

            }
            biayaLainRef.addValueEventListener(valueListener)

            awaitClose {
                biayaLainRef.removeEventListener(valueListener)
            }
        }
    }

    override suspend fun getSingle(jenisBiaya: String): Result<BiayaLainModel?> {
        return callbackFlow<Result<BiayaLainModel?>> {
            biayaLainRef.child(jenisBiaya)
                .get()
                .addOnSuccessListener { snapshot ->
                    val biayaLain = snapshot.getValue<BiayaLainModel>()

                    trySendBlocking(Result.success(biayaLain))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun addBiaya(model: BiayaLainModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            biayaLainRef.child(model.jenisBiaya)
                .setValue(model)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun delete(model: BiayaLainModel): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            biayaLainRef.child(model.jenisBiaya)
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

    override suspend fun update(
        oldModel: BiayaLainModel,
        newModel: BiayaLainModel,
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            // First, we delete the old value
            biayaLainRef.child(oldModel.jenisBiaya)
                .removeValue()
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            // Then, we add the new value
            biayaLainRef.child(newModel.jenisBiaya)
                .setValue(newModel)
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