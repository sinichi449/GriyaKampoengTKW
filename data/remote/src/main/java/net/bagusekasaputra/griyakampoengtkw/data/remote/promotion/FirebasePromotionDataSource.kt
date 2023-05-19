package net.bagusekasaputra.griyakampoengtkw.data.remote.promotion

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePromotionDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PromotionModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebasePromotionDataSource(
    databaseReference: DatabaseReference
): RemotePromotionDataSource {

    private val promotionRef = databaseReference.child(FirebaseNodes.PROMOTION)

    override suspend fun get(): Result<PromotionModel?> {
        return suspendCoroutine { continuation ->
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue<PromotionModel>()

                    continuation.resume(Result.success(model))
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()

                    continuation.resume(Result.failure(exception))
                }
            }

            promotionRef.addListenerForSingleValueEvent(eventListener)
        }
    }

}