package net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.FirebaseNodes

class FirebaseHargaKavlingSource(
    private val databaseReference: DatabaseReference
): RemoteHargaKavlingSource {

    private val hargaKavlingRef = databaseReference.child(FirebaseNodes.HARGA_KAVLING)

    override fun getHargaKavlingModel(kavlingKode: String): Flow<Result<HargaKavlingModel?>> {
        return callbackFlow {
            hargaKavlingRef
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val hargaKavlingModel = snapshot.getValue<HargaKavlingModel>()

                    trySendBlocking(Result.success(hargaKavlingModel))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Flow<Result<Boolean>> {
        return callbackFlow {
            hargaKavlingRef
                .child(hargaKavlingModel.kavlingKode)
                .setValue(hargaKavlingModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
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