package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.FirebaseNodes

class FirebasePembayaranSource(
    private val databaseReference: DatabaseReference
): RemotePembayaranSource {

    private val pembayaranRef = databaseReference.child(FirebaseNodes.FORM_PEMBAYARAN)

    override suspend fun getAllPembayaran(
        kavlingKode: String,
        onSuccess: (listPembayaranModel: List<PembayaranModel>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    ) {
        pembayaranRef
            .child(kavlingKode)
            .get()
            .addOnSuccessListener { snapshot ->
                val terminHashMap = snapshot.getValue<HashMap<String, PembayaranModel>>()

                if (terminHashMap != null) {
                    val listPembayaranModel = ArrayList<PembayaranModel>()
                    for (key in terminHashMap.keys) {
                        terminHashMap[key]?.let {
                            listPembayaranModel.add(it)
                        }
                    }

                    onSuccess(listPembayaranModel)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    override suspend fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        val terminChild = getTerminChild(pembayaranModel.termin, pembayaranModel.urutan)

        // check if child is available to avoid replacing the available data.
        // if the user intended to replace, he must go through edit.
        isTerminChildAvailable(kavlingKode, terminChild) { available ->
            if (available) {
                onFailure(UnknownError("Child sudah ada di database!"))
            } else {
                pembayaranRef
                    .child(kavlingKode)
                    .child(terminChild)
                    .setValue(pembayaranModel)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it.cause?: UnknownError("Terjadi kesalahan!"))
                    }
            }
        }
    }

    override suspend fun updatePembayaranModel(
        kavlingKode: String,
        oldPembayaranModel: PembayaranModel,
        newPembayaranModel: PembayaranModel,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    ) {
        // First we need to delete the old data first
        pembayaranRef
            .child(kavlingKode)
            .child(getTerminChild(oldPembayaranModel.termin, oldPembayaranModel.urutan))
            .removeValue()

        // Then we set new pembayaran value, and set the child as new child
        pembayaranRef
            .child(kavlingKode)
            .child(getTerminChild(newPembayaranModel.termin, newPembayaranModel.urutan))
            .setValue(newPembayaranModel)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{ onFailure(it.cause?: UnknownError("Terjadi kesalahan mengubah pembayaran")) }
    }

    override suspend fun deletePembayaranModelByTermin(
        kavlingKode: String,
        termin: String,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        pembayaranRef
            .child(kavlingKode)
            .child(termin)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onFailure(it.cause?: UnknownError("Terjadi kesalahan menghapus pembayaran"))
            }
    }

    override suspend fun deleteAllPembayaranModel(
        kavlingKode: String,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    ) {
        pembayaranRef
            .child(kavlingKode)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onFailure(it.cause?: UnknownError("Terjadi kesalahan menghapus semua pembayaran kalving $kavlingKode"))
            }
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