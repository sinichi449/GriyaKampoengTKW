package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranTambahLuasanModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

@OptIn(ExperimentalCoroutinesApi::class)
class FirebasePembayaranTambahLuasanDataSource(
    databaseReference: DatabaseReference,
): RemotePembayaranTambahLuasanDataSource {

    private val ref = databaseReference.child(FirebaseNodes.PEMBAYARAN_TAMBAH_LUASAN)

    override suspend fun getAll(kavling: String): Result<List<PembayaranTambahLuasanModel>?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = ref.child(kavling),
            onGetSnapshot = { snapshot ->
                val kavlingMap = snapshot.getValue<HashMap<String, PembayaranTambahLuasanModel>>()

                if (kavlingMap != null) {
                    val data = ArrayList<PembayaranTambahLuasanModel>()
                    for (key in kavlingMap.keys) {
                        kavlingMap[key]?.let {
                            data.add(it)
                        }
                    }

                    return@getOperation data
                } else {
                    return@getOperation null
                }
            },
            timeOutMsg = "Waktu habis mendapatkan Pembayaran Tambah Luasan",
            onClosedConnection = {}
        )
    }

    override suspend fun add(model: PembayaranTambahLuasanModel): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            ref.child(model.kavling).child(model.id)
                .setValue(model)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(null), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it), null)
                    }
                }
        }
    }
}