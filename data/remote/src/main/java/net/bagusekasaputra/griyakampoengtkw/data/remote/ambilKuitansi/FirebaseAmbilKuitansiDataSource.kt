package net.bagusekasaputra.griyakampoengtkw.data.remote.ambilKuitansi

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.AmbilKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAmbilKuitansiDataSource(
    databaseReference: DatabaseReference,
): RemoteAmbilKuitansiDataSource {

    private val ambilKuitansiRef = databaseReference.child(FirebaseNodes.AMBIL_KUITANSI)

    override suspend fun get(kavling: String, termin: String): Result<AmbilKuitansiModel?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sudahAmbil = snapshot.getValue<Boolean>() ?: false

                    val model = AmbilKuitansiModel(kavling, termin, sudahAmbil)
                    continuation.resume(Result.success(model))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    continuation.resume(Result.failure(exception))
                }
            }

            ambilKuitansiRef.child("${kavling}/${termin}")
                .addListenerForSingleValueEvent(eventListener)
        }
    }

}