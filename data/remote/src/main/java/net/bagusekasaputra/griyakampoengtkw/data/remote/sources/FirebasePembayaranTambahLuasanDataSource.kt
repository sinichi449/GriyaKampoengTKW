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

    override suspend fun get(kavling: String, id: String): Result<PembayaranTambahLuasanModel?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = ref.child(kavling).child(id),
            onGetSnapshot = { snapshot ->
                return@getOperation snapshot.getValue<PembayaranTambahLuasanModel>()
            },
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

    override suspend fun update(
        id: String,
        newModel: PembayaranTambahLuasanModel
    ): Result<Nothing?> {
        val deleteOperation = deleteById(newModel.kavling, id)
        return if (deleteOperation.isSuccess) {
            add(newModel)
        } else {
            Result.failure(deleteOperation.exceptionOrNull() ?: Exception("Terjadi kesalahan override data!"))
        }
    }

    override suspend fun deleteById(kavling: String, id: String): Result<Nothing?> {
        return suspendCancellableCoroutine { continuation ->
            ref.child(kavling).child(id)
                .removeValue()
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