package net.bagusekasaputra.griyakampoengtkw.data.remote.sources

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTahapanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.TahapanModel

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseTahapanDataSource(
    private val rootDatabaseReference: DatabaseReference,
): RemoteTahapanDataSource {

    override suspend fun getAll(): Result<List<TahapanModel>?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val tahapanList = mutableListOf<TahapanModel>()

                        snapshot.children.forEach {
                            val tahapanKey = it.key

                            if (tahapanKey?.startsWith("TAHAP_", false) == true) {
                                tahapanList.add(TahapanModel(tahapanKey))
                            }
                        }

                        continuation.resume(Result.success(tahapanList), null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error.toException()), null)
                    }
                }
            }

            rootDatabaseReference.addListenerForSingleValueEvent(eventListener)
        }
    }

}