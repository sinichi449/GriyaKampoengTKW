package net.bagusekasaputra.griyakampoengtkw.data.remote_backup

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.FirebaseNodes.backupNodeOf

@OptIn(ExperimentalCoroutinesApi::class)
class BackupFirebaseBlokDataSource(
    backupName: String,
    databaseReference: DatabaseReference,
): BackupBlokDataSource {

    private val blokRef by lazy {
        databaseReference.backupNodeOf(backupName, FirebaseNodes.BLOCKS)
    }

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val bloks = buildList {
                            snapshot.children.forEach { blokSnapshot ->
                                val item = blokSnapshot.getValue<BlockModel>()
                                item?.also { add(it) }
                            }
                        }

                        val result = bloks.ifEmpty { null }
                        continuation.resume(
                            value = Result.success(result),
                            onCancellation = null
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error.toException()), null)
                    }
                }

            }

            blokRef.addListenerForSingleValueEvent(eventListener)
        }
    }

}