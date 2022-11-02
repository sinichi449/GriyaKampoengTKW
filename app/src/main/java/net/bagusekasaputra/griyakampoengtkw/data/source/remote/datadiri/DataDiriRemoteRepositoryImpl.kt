package net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDiriRemoteRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference
): IDataDiriRemoteRepository {

    override fun getDataDiri(kavlingKode: String): Flow<Result<DataDiriFirebaseModel?>> {
        return callbackFlow {
            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val blockKode = kavlingKode[0].toString()

                    val result = snapshot.child(GriyaNodes.blocks).child(blockKode).child(kavlingKode).child(GriyaNodes.dataDiri).getValue(
                        DataDiriFirebaseModel::class.java)

                    trySendBlocking(Result.success(result))
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().cause?.let {
                        trySendBlocking(Result.failure(it))
                    }
                }
            }

            databaseReference.addValueEventListener(postListener)

            awaitClose {
                databaseReference.removeEventListener(postListener)
            }
        }
    }
}