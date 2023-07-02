package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMaterialPembangunanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MaterialPembangunanModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebaseMaterialPembangunanDataSource(
    private val databaseReference: DatabaseReference
): RemoteMaterialPembangunanDataSource {

    override suspend fun getAll(
        untuk: String,
        kategori: String
    ): Result<List<MaterialPembangunanModel>?> {
        return FirebaseRequestHelper.readDataOnce(
            reference = getReference(databaseReference, untuk, kategori),
            withDataReceived = { snapshot ->
                val materialPembangunan = mutableListOf<MaterialPembangunanModel>()

                snapshot.children.forEach { data ->
                    data.getValue<MaterialPembangunanModel>()?.also { model ->
                        materialPembangunan.add(model)
                    }
                }

                Result.success(materialPembangunan.ifEmpty { null })
            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun insert(model: MaterialPembangunanModel): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val reference = getReference(databaseReference, model.untuk, model.kategori)
                .child(model.keyId)

            reference.setValue(model)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit), null)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(it), null)
                    }
                }
        }
    }

    override fun getTableName(): String {
        return "materialPembangunan"
    }

    private fun getReference(
        databaseReference: DatabaseReference,
        untuk: String,
        kategori: String
    ): DatabaseReference {
        return databaseReference
            .child(FirebaseNodes.MATERIAL_PEMBANGUNAN)
            .child(kategori)
            .child(untuk)
    }
}