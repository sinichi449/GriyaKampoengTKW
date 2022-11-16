package net.bagusekasaputra.griyakampoengtkw.data.source.remote.biayaMarketing

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBiayaMarketingDataSource @Inject constructor(
    databaseReference: DatabaseReference,
): RemoteBiayaMarketingDataSource {

    private val biayaMarketingRef = databaseReference.child(GriyaNodes.biayaMarketing)

    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?> {
        return callbackFlow<Result<List<BiayaMarketingModel>?>> {
            biayaMarketingRef
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val nomorHashMap = snapshot.getValue<HashMap<String, BiayaMarketingModel>>()

                    if (nomorHashMap != null) {
                        val listBiayaMarketingModel = ArrayList<BiayaMarketingModel>()

                        for (key in nomorHashMap.keys) {
                            nomorHashMap[key]?.let {
                                listBiayaMarketingModel.add(it)
                            }
                        }

                        trySendBlocking(Result.success(listBiayaMarketingModel))
                    } else {
                        trySendBlocking(Result.success(null))
                    }
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            biayaMarketingRef
                .child(kavlingKode)
                .child(biayaMarketingModel.timeMillis.toString())
                .setValue(biayaMarketingModel)
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
        kavlingKode: String,
        oldBiayaMarketingModel: BiayaMarketingModel,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            biayaMarketingRef
                .child(kavlingKode)
                .child(oldBiayaMarketingModel.timeMillis.toString())
                .setValue(newBiayaMarketingModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(null))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    override suspend fun deleteSingle(kavlingKode: String, nomor: Int): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}