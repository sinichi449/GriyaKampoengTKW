package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes

class FirebaseAppUpdateSource(
    databaseReference: DatabaseReference
): RemoteAppUpdateSource {

    private val updateRef = databaseReference.child(FirebaseNodes.UPDATE)

    override suspend fun getUpdateInformation(): Result<AppUpdateModel?> {
        Log.d("FIREBASE_URL", "App update is at $updateRef")
        return callbackFlow<Result<AppUpdateModel?>> {
            updateRef.get()
                .addOnSuccessListener { snapshot ->

                    val appUpdateModel = snapshot.getValue<AppUpdateModel>()

                    trySendBlocking(Result.success(appUpdateModel))

                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private fun mapReleaseNotes(firebaseReleaseNotes: Map<String, String>): List<String> {
        val notesList = ArrayList<String>()

        firebaseReleaseNotes.keys.forEach { index ->
            firebaseReleaseNotes[index]?.let {
                notesList.add(it)
            }
        }

        return notesList
    }

    private data class FirebaseAppUpdateModel(
        val latestVersion: String = "",
        val latestVersionCode: Int = 0,
        val url: String = "",
        val releaseNotes: java.util.ArrayList<String> = java.util.ArrayList<String>(),
    )

}