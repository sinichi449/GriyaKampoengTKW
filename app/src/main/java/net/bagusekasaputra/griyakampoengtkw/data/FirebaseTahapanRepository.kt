package net.bagusekasaputra.griyakampoengtkw.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bagusekasaputra.griyakampoengtkw.model.Tahapan
import net.bagusekasaputra.griyakampoengtkw.repository.TahapanRepository

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseTahapanRepository(
    private val rootDatabaseReference: DatabaseReference,
): TahapanRepository {

    override suspend fun getAllTahapan(): Result<List<Tahapan>?> {
        return suspendCancellableCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (continuation.isActive) {
                        val tahapanList = mutableListOf<Tahapan>()

                        snapshot.children.forEach {
                            val tahapanKey = it.key

                            if (tahapanKey?.startsWith("TAHAP_", false) == true) {
                                tahapanList.add(Tahapan(tahapanKey))
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