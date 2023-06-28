package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseKavlingDataSource(
    private val databaseReference: DatabaseReference
): RemoteKavlingDataSource {

    private val kavlingRef = databaseReference.child(FirebaseNodes.KAVLINGS)

    override suspend fun getAllKavlings(blockKode: String): Result<List<KavlingModel>?> {
        Log.d("FIREBASE_URL", "Kavling is at $kavlingRef")

        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kavlingList = buildList {
                        snapshot.children.forEach { child ->
                            val dataKavling = child.value as Map<String, Any?>?
                            val result = KavlingModel(
                                active = dataKavling?.get("active") as Boolean? ?: true,
                                kode = dataKavling?.get("kode") as String? ?: "",
                                type = dataKavling?.get("type") as String? ?: "",
                                ukuran = dataKavling?.get("ukuran") as String? ?: "",
                                warna = dataKavling?.get("warna") as String? ?: "",
                                isCombined = dataKavling?.get("isCombined") as Boolean? ?: false,
                            )

                            add(result)
                        }
                    }

                    continuation.resume(
                        Result.success(kavlingList.ifEmpty { null }),
                        null
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Result.failure(error.toException()), null)
                }
            }

            kavlingRef.child(blockKode).addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun addKavling(
        blockKode: String,
        kavlingModel: KavlingModel
    ): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = kavlingRef.child(blockKode).child(kavlingModel.kode),
            valueToInsert = kavlingModel,
        )
    }

    override suspend fun updateKavling(
        blockKode: String,
        oldKavling: KavlingModel,
        newKavling: KavlingModel
    ): Result<Nothing?> {
        return FirebaseRequestHelper.updateOperation(
            targetChild = kavlingRef.child(blockKode).child(oldKavling.kode),
            newValue = newKavling,
        )
    }

    override suspend fun deleteKavling(blockKode: String, kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = kavlingRef.child(blockKode).child(kavlingKode)
        )
    }

    override suspend fun setKavlingBelumDiisi(kavlingKode: String, belumIsi: Boolean) {
        val blockKode = kavlingKode.substring(0, 1)

        // active -> true       : would mean that the kavling doesn't have data diri
        // active -> false      : would mean that the kavling filled with data diri
        kavlingRef
            .child(blockKode)
            .child(kavlingKode)
            .child("active")
            .setValue(belumIsi)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    override suspend fun getUnmigratedKavlings(backupName: String): Result<List<String>?> {
        val pathToFirebaseChild = "${FirebaseNodes.BACKUPS}/$backupName/${FirebaseNodes.UNMIGRATED}"
        return FirebaseRequestHelper.getOperation(
            pathToChild = databaseReference.child(pathToFirebaseChild),
            onGetSnapshot = {
                val listUnmigratedKavling = it.getValue<List<String>>()

                listUnmigratedKavling
            },
            timeOutMsg = "Waktu habis mendapatkan Kavling Data Lama!",
            onClosedConnection = {}
        )
    }

    override suspend fun getRekapExclusionList(): Result<List<String>?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val exclusionList = snapshot.getValue<List<String>>()

                        continuation.resume(Result.success(exclusionList), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error.toException()), null)
                    }
                }
            }

            databaseReference.child(FirebaseNodes.KAVLING_EXCLUSION_LIST)
                .addListenerForSingleValueEvent(eventListener)
        }
    }

    private fun isKavlingExists(blockKode: String, kavling: KavlingModel): Flow<Boolean> {
        return callbackFlow {
            kavlingRef
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