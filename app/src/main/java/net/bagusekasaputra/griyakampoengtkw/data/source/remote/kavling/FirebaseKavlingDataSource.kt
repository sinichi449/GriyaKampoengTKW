package net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.FirebaseRequestHelper
import net.bagusekasaputra.griyakampoengtkw.logEvent
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseKavlingDataSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteKavlingDataSource {

    private val kavlingRef = databaseReference.child(GriyaNodes.kavlings)

    override suspend fun getAllKavlings(blockKode: String): Result<List<KavlingModel>?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = kavlingRef.child(blockKode),
            onGetSnapshot = { snapshot ->
                val hashMap = snapshot.getValue<HashMap<String, KavlingModel>>()
                val kavlings = ArrayList<KavlingModel>()

                hashMap?.keys?.forEach { key ->
                    hashMap[key]?.let { model ->
                        kavlings.add(model)
                    }
                }

                return@getOperation kavlings
            },
            timeOutMsg = "Waktu habis mendapatkan kavling, periksa koneksi Anda.",
            onClosedConnection = {},
        )
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

//    override fun removeKavling(blockKode: String, kavlingKode: String): Flow<Result<Boolean>> {
//        return callbackFlow {
//            databaseReference
//                .child(GriyaNodes.kavlings)
//                .child(blockKode)
//                .child(kavlingKode)
//                .removeValue()
//                .addOnSuccessListener {
//                    trySendBlocking(Result.success(true))
//                }
//                .addOnFailureListener {
//                    trySendBlocking(Result.failure(it))
//                }
//
//            awaitClose {  }
//        }
//    }
    override suspend fun deleteKavling(blockKode: String, kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = kavlingRef.child(blockKode).child(kavlingKode)
        )
    }

    override suspend fun setKavlingBelumDiisi(kavlingKode: String, belumIsi: Boolean) {
        val blockKode = kavlingKode.substring(0, 1)

        // active -> true       : would mean that the kavling doesn't have data diri
        // active -> false      : would mean that the kavling filled with data diri
        logEvent("Changing active in block $blockKode and kavling $kavlingKode as $belumIsi")
        kavlingRef
            .child(blockKode)
            .child(kavlingKode)
            .child("active")
            .setValue(belumIsi)
            .addOnCompleteListener {
                logEvent("Change active is successfull")
            }
            .addOnFailureListener {
                logEvent("Change is failed ${it.message}")
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