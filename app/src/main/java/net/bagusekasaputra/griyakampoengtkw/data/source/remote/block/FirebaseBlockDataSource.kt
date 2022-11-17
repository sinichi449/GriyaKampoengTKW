package net.bagusekasaputra.griyakampoengtkw.data.source.remote.block

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.FirebaseRequestHelper
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseBlockDataSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemoteBlockDataSource {

    private val blockRef = databaseReference.child(GriyaNodes.blocks)

    override suspend fun getAllBlocks(): Result<List<BlockModel>?> {
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