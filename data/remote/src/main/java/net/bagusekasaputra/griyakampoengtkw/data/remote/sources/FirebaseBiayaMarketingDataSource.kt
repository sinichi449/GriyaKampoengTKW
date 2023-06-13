package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseBiayaMarketingDataSource(
    private val databaseReference: DatabaseReference,
): RemoteBiayaMarketingDataSource {

    private val biayaMarketingRef = databaseReference.child(FirebaseNodes.BIAYA_MARKETING)

    // Path to child => biayaMarketing/$kavling/$jenisBiaya

    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = biayaMarketingRef.child(kavlingKode),
            onGetSnapshot = { snapshot ->
                val biayaMarketingMap = snapshot.getValue<HashMap<String, BiayaMarketingModel>>()

                val listBiayaMarketing = mutableListOf<BiayaMarketingModel>()
                biayaMarketingMap?.keys?.forEach { jenisBiaya ->
                    val biayaMarketing = biayaMarketingMap[jenisBiaya]

                    biayaMarketing?.let {
                        listBiayaMarketing.add(it)
                    }
                }

                listBiayaMarketing
            },
            timeOutMsg = "Waktu habis mendapatkan biaya marketing dari server",
            onClosedConnection = {},
        )
    }

    override suspend fun getFromBackup(
        backupName: String,
        kavlingKode: String
    ): Result<List<BiayaMarketingModel>?> {
        return suspendCoroutine { continuation ->
            val backupBiayaMarketingRef = FirebaseNodes
                .getBackupNode(databaseReference, backupName, FirebaseNodes.BIAYA_MARKETING)
                .child(kavlingKode)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mapBiayaMarketing = snapshot.getValue<HashMap<String, BiayaMarketingModel>>()
                    val listBiayaMarketing = mutableListOf<BiayaMarketingModel>()

                    mapBiayaMarketing?.keys?.forEach { namaBiaya ->
                        val biayaMarketing = mapBiayaMarketing[namaBiaya]
                        if (biayaMarketing != null) {
                            listBiayaMarketing.add(biayaMarketing)
                        }
                    }

                    continuation.resume(Result.success(listBiayaMarketing))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            }

            backupBiayaMarketingRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        val jenisBiayaChild = biayaMarketingModel.jenisBiaya
        Log.d("DEBUG_ME", "Inserting biaya marketing in firebase data source")
        return FirebaseRequestHelper.insertOperation(
            targetChild = biayaMarketingRef
                .child(kavlingKode)
                .child(jenisBiayaChild),
            valueToInsert = biayaMarketingModel,
        )
    }

    override suspend fun update(
        kavlingKode: String,
        oldBiayaMarketingModel: BiayaMarketingModel,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        val oldJenisBiayaChild = oldBiayaMarketingModel.jenisBiaya
        val newJenisBiayaChild = newBiayaMarketingModel.jenisBiaya

        return FirebaseRequestHelper.updateOperationAndSetNewChild(
            toBeDeletedChild = biayaMarketingRef
                .child(kavlingKode)
                .child(oldJenisBiayaChild),
            newChild = biayaMarketingRef
                .child(kavlingKode)
                .child(newJenisBiayaChild),
            newValue = newBiayaMarketingModel,
        )
    }

    override suspend fun deleteSingle(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        val jenisBiayaChild = biayaMarketingModel.jenisBiaya

        return FirebaseRequestHelper.deleteOperation(
            targetChild = biayaMarketingRef
                .child(kavlingKode)
                .child(jenisBiayaChild)
        )
    }

    override suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = biayaMarketingRef.child(kavlingKode)
        )
    }


}