package net.bagusekasaputra.griyakampoengtkw.data.remote.metadata

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

class FirebaseMetadataDataSource(
    databaseReference: DatabaseReference,
): RemoteMetadataDataSource {

    private val metadataRef = databaseReference.child(FirebaseNodes.METADATA_ROOT)

    override suspend fun get(tableName: String): MetadataModel? {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val metadata = snapshot.getValue<MetadataModel>()

                    trySendBlocking(metadata)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            }

            val targetRef = metadataRef.child(tableName)
            targetRef.addListenerForSingleValueEvent(listener)

            awaitClose {
                targetRef.removeEventListener(listener)
            }
        }.first()
    }

    override suspend fun update(oldMetadataModel: MetadataModel, newMetadataModel: MetadataModel) {
        // Remove old value first
        delete(oldMetadataModel.tableName)

        // Then insert the new one
        metadataRef.child(oldMetadataModel.tableName)
            .setValue(newMetadataModel)
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "Success updating metadata \"${oldMetadataModel.tableName}\"")
            }
            .addOnFailureListener {
                throw it
            }
    }

    override suspend fun delete(tableName: String) {
        metadataRef.child(tableName)
            .removeValue()
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "Deleting metadata \"$tableName\" success")
            }
            .addOnFailureListener {
                throw it
            }
    }
}