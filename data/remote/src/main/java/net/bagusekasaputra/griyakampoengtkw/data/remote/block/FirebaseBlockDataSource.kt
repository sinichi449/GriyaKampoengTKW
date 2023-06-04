package net.bagusekasaputra.griyakampoengtkw.data.remote.block

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper

class FirebaseBlockDataSource(
    databaseReference: DatabaseReference
): RemoteBlockDataSource {

    private val blockRef = databaseReference.child(FirebaseNodes.BLOCKS)

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
        Log.d("INIT_CACHE", "Using ${blockRef.parent?.toString()} as root reference!")

        return FirebaseRequestHelper.getOperation(
            pathToChild = blockRef,
            onGetSnapshot = { snapshot ->
                val hashMap = snapshot.getValue<HashMap<String, BlockModel>>()
                // An empty list for container
                val blockModels = ArrayList<BlockModel>()

                hashMap?.keys?.forEach { keys ->
                    if (hashMap[keys] != null) {
                        blockModels.add(hashMap[keys]!!)
                    }
                }

                return@getOperation blockModels
            },
            timeOutMsg = "Waktu habis mendapatkan Blocks, periksa koneksi Anda.",
            onClosedConnection = {},
        )
    }

    override suspend fun addNewBlock(blockModel: BlockModel): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = blockRef.child(blockModel.kode),
            valueToInsert = blockModel,
        )
    }

    private fun isBlockAlreadyExist(blockKode: String): Flow<Boolean> {
        return callbackFlow {
            blockRef
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