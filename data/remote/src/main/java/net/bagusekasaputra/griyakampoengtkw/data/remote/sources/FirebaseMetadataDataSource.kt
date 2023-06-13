package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

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
        Log.d("DEBUG_ME", "FirebaseMetadataSource->update(): Invoked")

        // Remove old value first
        delete(oldMetadataModel.tableName)

        // Then insert the new one
        metadataRef.child(oldMetadataModel.tableName)
            .setValue(newMetadataModel.let {
                // If the update operation contains multiple child, i.e containing "/" character
                val containMultipleChild = it.tableName.contains("/")
                if (containMultipleChild) {
                    Log.d("DEBUG_ME", "FirebaseMetadataSource->update(): The requested table \"${newMetadataModel.tableName}\" containing \"/\" character, suggesting a multiple child mode.")
                    // Only get the first index
                    // For example: "images_foto_pembayaran/D1" becomes "images_foto_pembayaran".
                    val onlyTableName = it.tableName.split("/")[0]
                    Log.d("DEBUG_ME", "FirebaseMetadataSource->update(): Changing table from \"${newMetadataModel.tableName} to \"$onlyTableName\".")
                    MetadataModel(onlyTableName, it.timestamp)
                } else {
                    it
                }
            })
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "Success updating metadata \"${oldMetadataModel.tableName}\"")
            }
            .addOnFailureListener {
                it.printStackTrace()

                Log.d("DEBUG_ME", "FAILED updating remote metadata for $newMetadataModel : ${it.message}")
            }
    }

    override suspend fun delete(tableName: String) {
        metadataRef.child(tableName)
            .removeValue()
            .addOnSuccessListener {
                Log.d("DEBUG_ME", "Deleting metadata \"$tableName\" success")
            }
            .addOnFailureListener {
                it.printStackTrace()

                Log.d("DEBUG_ME", "FirebaseMetadataDataSource->delete(): FAILED to delete table $tableName : ${it.message}")
            }
    }
}