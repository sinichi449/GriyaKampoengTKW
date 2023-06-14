package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper.readDataOnce
import net.bagusekasaputra.griyakampoengtkw.data.remote.model.PengembalianFirebaseModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.model.PengembalianFirebaseModel.Companion.toDataModel

@OptIn(ExperimentalCoroutinesApi::class)
class FirebasePengembalianDataSource(
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference,
): RemotePengembalianDataSource {

    private val pengembalianRef by lazy {
        databaseReference.child(FirebaseNodes.PENGEMBALIAN_PEMBAYARAN)
    }

    private val imageRef by lazy {
        storageReference.child(FirebaseNodes.IMAGE_PENGEMBALIAN)
    }

    init {
        Log.d("FIREBASE_URL", "Pengembalian is at $pengembalianRef")
        Log.d("FIREBASE_URL", "Image Pengembalian is at $imageRef")
    }

    override suspend fun get(keyId: String) = readDataOnce(
        reference = pengembalianRef.child(keyId),
        withDataReceived = { snapshot ->
            val model = snapshot.getValue<PengembalianFirebaseModel>()
            val pengembalian = model?.toDataModel(keyId, "")

            Result.success(pengembalian)
        }
    )

    override suspend fun getKeyIds() = readDataOnce(
        reference = pengembalianRef,
        withDataReceived = { snapshot ->
            val keyIds = buildList {
                snapshot.children.forEach {
                    it.key?.let { key -> add(key) }
                }
            }
            if (keyIds.isEmpty()) Result.success(null)
            else Result.success(keyIds)
        }
    )

    override suspend fun insert(model: PengembalianModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(keyId: String, newModel: PengembalianModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(keyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadImage(keyId: String, saveUri: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            imageRef.child(PengembalianModel.getFilename(keyId))
                .getFile(Uri.parse(saveUri))
                .addOnCompleteListener {
                    continuation.resume(true, null)
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    continuation.resume(false, null)
                }
        }
    }

    override fun getTableName(): String {
        return FirebaseNodes.PENGEMBALIAN_PEMBAYARAN
    }
}