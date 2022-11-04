package net.bagusekasaputra.griyakampoengtkw.data.source.remote.block

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBlockRepository @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteBlockRepository {

    init {
        databaseReference.child(GriyaNodes.blocks).keepSynced(true)
    }

    override fun getAllBlocks(): Flow<List<BlockModel>?> {
        Log.d(LOG_TAG, "Getting all blocks from firebase ...")

        return callbackFlow {
            databaseReference
                .child(GriyaNodes.blocks)
                .get()
                .addOnSuccessListener { snapshot ->
                    Log.d(LOG_TAG, "Getting blocks success.")
                    val hashMap = snapshot.getValue<HashMap<String, BlockModel>>()
                    val blockModels = ArrayList<BlockModel>()
                    hashMap?.keys?.forEach { keys ->
                        if (hashMap[keys] != null) {
                            blockModels.add(hashMap[keys]!!)
                        }
                    }

                    trySendBlocking(blockModels)
                }
                .addOnFailureListener {
                    Log.d(LOG_TAG, "Error getting blocks: ${it.message}")
                }


            awaitClose { }
        }
    }

    override fun addNewBlock(blockModel: BlockModel): Flow<Result<Boolean>> {
        Log.d(LOG_TAG, "Sending ${blockModel.kode} to Firebase...")

        return callbackFlow {
            databaseReference
                .child(GriyaNodes.blocks)
                .child(blockModel.kode)
                .setValue(blockModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                    Log.d(LOG_TAG, "Write new block success")
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                    Log.d(LOG_TAG, "Write new block failed: ${it.message}")
                }

            awaitClose {  }
        }
    }

    private fun isBlockAlreadyExist(blockKode: String): Flow<Boolean> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.blocks)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.hasChild(blockKode)) {
                        trySendBlocking(true)
                    } else {
                        trySendBlocking(false)
                    }
                }

            awaitClose {  }
        }
    }
}