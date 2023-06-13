package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseBackupRestoreDataSource(
    databaseReference: DatabaseReference
): RemoteBackupRestoreDataSource {

    private val backupRef = databaseReference.child(FirebaseNodes.BACKUPS)

    override suspend fun getListBackup(): Result<List<String>?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listBackup = mutableListOf<String>()

                    snapshot.children.forEach {
                        val backupName = it.key
                        if (backupName != null) {
                            listBackup.add(backupName)
                        }
                    }

                    continuation.resume(Result.success(listBackup))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            }

            backupRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    override fun getTableName(): String {
        return FirebaseNodes.BACKUPS
    }
}