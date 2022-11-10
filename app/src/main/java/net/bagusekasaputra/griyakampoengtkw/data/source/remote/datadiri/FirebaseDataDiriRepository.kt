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
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataDiriRepository @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteDataDiriRepository {

    private val dataDiriRef = databaseReference.child(GriyaNodes.dataDiri)

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
            databaseReference
                .child(GriyaNodes.dataDiri)
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
            databaseReference
                .child(GriyaNodes.dataDiri)
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