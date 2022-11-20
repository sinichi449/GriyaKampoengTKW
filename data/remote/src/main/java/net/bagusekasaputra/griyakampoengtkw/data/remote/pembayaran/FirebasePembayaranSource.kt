package net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebasePembayaranSource(
    databaseReference: DatabaseReference
): RemotePembayaranSource {

    private val pembayaranRef = databaseReference.child(FirebaseNodes.FORM_PEMBAYARAN)

    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = pembayaranRef.child(kavlingKode),
            onGetSnapshot = { snapshot ->
                val terminHashMap = snapshot.getValue<HashMap<String, PembayaranModel>>()

                if (terminHashMap != null) {
                    val listPembayaranModel = ArrayList<PembayaranModel>()
                    for (key in terminHashMap.keys) {
                        terminHashMap[key]?.let {
                            listPembayaranModel.add(it)
                        }
                    }
                    return@getOperation listPembayaranModel
                } else {
                    return@getOperation null
                }
            },
            timeOutMsg = "Waktu habis mendapatkan data pembayaran",
            onClosedConnection = {},
        )
    }

    override suspend fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        val terminChild = getTerminChild(pembayaranModel.termin, pembayaranModel.urutan)

        // Check if child is available to avoid replacing the available data.
        // if the user intended to replace, he must go through edit.
        return FirebaseRequestHelper.insertOperationAlertOverwrite(
            targetChild = pembayaranRef
                .child(kavlingKode)
                .child(terminChild),
            valueToInsert = pembayaranModel,
            existMsg = "Termin sudah ada!",
        )
    }

    override suspend fun updatePembayaranModel(
        kavlingKode: String,
        oldPembayaranModel: PembayaranModel,
        newPembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        val terminChild = getTerminChild(oldPembayaranModel.termin, oldPembayaranModel.urutan)

        // This helper automatically remove the existing data before adding the new one.
        return FirebaseRequestHelper.updateOperation(
            targetChild = pembayaranRef
                .child(kavlingKode)
                .child(terminChild),
            newValue = newPembayaranModel,
        )
    }

    override suspend fun deletePembayaranModelByTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = pembayaranRef.child(kavlingKode).child(termin)
        )
    }

    override suspend fun deleteAllPembayaranModel(kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = pembayaranRef.child(kavlingKode),
        )
    }

    private fun isTerminChildAvailable(
        kavlingKode: String,
        terminChild: String,
        onSuccess: (available: Boolean) -> Unit,
    ) {
        pembayaranRef
            .child(kavlingKode)
            .child(terminChild)
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.getValue<PembayaranModel>()

                if (data == null) onSuccess(false)
                else onSuccess(true)
            }
    }

    private fun getTerminChild(termin: String, urutan: Int): String {
        return "$termin $urutan"
    }

}