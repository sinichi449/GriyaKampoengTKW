package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePembayaranSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemotePembayaranSource {

    override fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.formPembayaran)
                .child(kavlingKode)
                .setValue(pembayaranModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }


}