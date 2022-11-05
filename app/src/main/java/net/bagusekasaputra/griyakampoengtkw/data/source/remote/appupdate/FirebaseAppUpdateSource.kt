package net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAppUpdateSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteAppUpdateSource {

    private val updateRef = databaseReference.child(GriyaNodes.update)

    override suspend fun getUpdateInformation(
        onSuccess: (appUpdateModel: AppUpdateModel) -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    ) {
        updateRef.get()
            .addOnSuccessListener { snapshot ->
                snapshot.getValue<FirebaseAppUpdateModel>()?.let {
                    onSuccess(
                        AppUpdateModel(
                            latestVersion = it.latestVersion,
                            latestVersionCode = it.latestVersionCode,
                            url = it.url,
                            releaseNotes = it.releaseNotes
                        )
                    )
                }
            }
            .addOnFailureListener {
                onFailure(it.cause?: UnknownError("Terjadi kesalahan saat mendapatkan update"))
            }
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