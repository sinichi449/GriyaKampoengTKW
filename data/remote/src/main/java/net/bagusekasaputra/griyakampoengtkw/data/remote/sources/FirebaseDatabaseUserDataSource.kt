package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DatabaseReference
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDatabaseUserDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DatabaseUserModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseUserDataSource(
    databaseReference: DatabaseReference
): RemoteDatabaseUserDataSource {

    private val databaseUserRef = databaseReference.child(FirebaseNodes.DATABASE_USER)

    override suspend fun getAll(): Result<DatabaseUserModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(model: DatabaseUserModel): Result<Nothing?> {
        return suspendCoroutine { continuation ->
            databaseUserRef.child(model.nama)
                .setValue(model)
                .addOnSuccessListener {
                    continuation.resume(Result.success(null))
                }
                .addOnFailureListener {
                    it.printStackTrace()

                    continuation.resumeWithException(it)
                }
        }
    }
}