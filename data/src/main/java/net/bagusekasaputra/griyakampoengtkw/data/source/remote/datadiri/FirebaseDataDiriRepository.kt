package net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.ConnectionUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.FirebaseNodes
import java.util.concurrent.atomic.AtomicBoolean

class FirebaseDataDiriRepository(
    private val databaseReference: DatabaseReference
): RemoteDataDiriRepository {

    private val dataDiriRef = databaseReference.child(FirebaseNodes.DATA_DIRI)

    override suspend fun getDataDiri(kavlingKode: String): Result<DataDiriModel?> {
        return callbackFlow<Result<DataDiriModel?>> {

            val gotResult = AtomicBoolean(false)

            dataDiriRef
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val model = snapshot.getValue<DataDiriModel>()

                    gotResult.set(true)

                    trySendBlocking(Result.success(model))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            ConnectionUtil.createRequestTimeout(
                gotResult = gotResult.get(),
                onTimeOut = {
                    trySendBlocking(Result.failure(UnknownError("Gagal mendapatkan data diri, periksa koneksi Anda")))
                }
            )

            awaitClose {

            }
        }.first()
    }

    override fun addDataDiri(
        kavlingKode: String,
        dataDiriModel: DataDiriModel,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            dataDiriRef
                .child(kavlingKode)
                .setValue(dataDiriModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose { }
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return callbackFlow {
            dataDiriRef
                .child(kavlingKode)
                .removeValue()
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