package net.bagusekasaputra.griyakampoengtkw.data.remote.hargakavling

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebaseHargaKavlingSource(
    databaseReference: DatabaseReference
): RemoteHargaKavlingSource {

    private val hargaKavlingRef = databaseReference.child(FirebaseNodes.HARGA_KAVLING)

    override suspend fun getHargaKavlingModel(kavlingKode: String): Result<HargaKavlingModel?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = hargaKavlingRef.child(kavlingKode),
            onGetSnapshot = { snapshot ->
                snapshot.getValue<HargaKavlingModel>()
            },
            timeOutMsg = "Waktu habis saat mendapatkan harga kavling dari server!",
            onClosedConnection = {},
        )
    }



    override suspend fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = hargaKavlingRef.child(hargaKavlingModel.kavlingKode),
            valueToInsert = hargaKavlingModel,
        )
    }

    override suspend fun deleteHargaKavlingModel(kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = hargaKavlingRef.child(kavlingKode),
        )
    }

    override fun getSingleHargaKavlingForPembayaran(
        kavlingKode: String,
        onSuccess: (harga: HargaKavlingModel?) -> Unit
    ) {
        hargaKavlingRef
            .child(kavlingKode)
            .get()
            .addOnSuccessListener { snapshot ->
                val hargaKavlingKavlingModel = snapshot.getValue<HargaKavlingModel>()

                onSuccess(hargaKavlingKavlingModel)
            }
    }

}