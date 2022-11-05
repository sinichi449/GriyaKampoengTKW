package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePembayaranSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemotePembayaranSource {

    override suspend fun getAllPembayaran(
        kavlingKode: String,
        onSuccess: (listPembayaranModel: List<PembayaranModel>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    ) {
        databaseReference
            .child(GriyaNodes.formPembayaran)
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
        databaseReference
            .child(GriyaNodes.formPembayaran)
            .child(kavlingKode)
            .child(pembayaranModel.termin)
            .setValue(pembayaranModel)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.cause?: UnknownError("Terjadi kesalahan!"))
            }
    }

    override suspend fun deletePembayaranModelByTermin(
        kavlingKode: String,
        termin: String,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit
    ) {
        databaseReference
            .child(GriyaNodes.formPembayaran)
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
        databaseReference
            .child(GriyaNodes.formPembayaran)
            .child(kavlingKode)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onFailure(it.cause?: UnknownError("Terjadi kesalahan menghapus semua pembayaran kalving $kavlingKode"))
            }
    }

}