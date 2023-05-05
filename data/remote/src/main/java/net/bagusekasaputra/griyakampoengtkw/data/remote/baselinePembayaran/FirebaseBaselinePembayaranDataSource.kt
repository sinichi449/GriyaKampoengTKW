package net.bagusekasaputra.griyakampoengtkw.data.remote.baselinePembayaran

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebaseBaselinePembayaranDataSource(
    databaseReference: DatabaseReference,
): RemoteBaselinePembayaranDataSource {

    private val baselinePembayaranRef = databaseReference.child(FirebaseNodes.BASELINE_PEMBAYARAN)

    override suspend fun get(kavling: String): Result<BaselinePembayaranModel?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = baselinePembayaranRef.child(kavling),
            onGetSnapshot = { snapshot ->
                val result = snapshot.getValue<BaselinePembayaranModel>()

                result
            },
            timeOutMsg = "Waktu habis mendapatkan Baseline Pembayaran",
            onClosedConnection = {}
        )
    }

    override suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = baselinePembayaranRef.child(model.kavling),
            valueToInsert = model,
        )
    }

}