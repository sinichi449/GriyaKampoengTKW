package net.bagusekasaputra.griyakampoengtkw.data.remote.datadiri

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.ConnectionUtil
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun getFromBackup(
        backupName: String,
        kavling: String
    ): Result<DataDiriModel?> {
        return suspendCoroutine { continuation ->
            val backupRef = FirebaseNodes
                .getBackupNode(databaseReference, backupName, FirebaseNodes.DATA_DIRI)
                .child(kavling)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataDiri = snapshot.getValue<DataDiriModel>()

                    continuation.resume(Result.success(dataDiri))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            }

            backupRef.addListenerForSingleValueEvent(eventListener)
        }
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