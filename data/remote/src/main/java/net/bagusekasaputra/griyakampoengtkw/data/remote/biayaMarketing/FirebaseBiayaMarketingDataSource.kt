package net.bagusekasaputra.griyakampoengtkw.data.remote.biayaMarketing

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebaseBiayaMarketingDataSource(
    databaseReference: DatabaseReference,
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