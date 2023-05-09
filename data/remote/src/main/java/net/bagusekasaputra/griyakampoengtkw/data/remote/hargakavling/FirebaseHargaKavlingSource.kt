package net.bagusekasaputra.griyakampoengtkw.data.remote.hargakavling

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseHargaKavlingSource(
    private val databaseReference: DatabaseReference
): RemoteHargaKavlingSource {

    private val hargaKavlingRef = databaseReference.child(FirebaseNodes.HARGA_KAVLING)

    override suspend fun getHargaKavlingModel(kavlingKode: String): Result<HargaKavlingModel?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = hargaKavlingRef.child(kavlingKode),
            onGetSnapshot = { snapshot ->
                snapshot.getValue<HargaKavlingModel>()
            },
            timeOutMsg = "Waktu habis saat mendapatkan harga kavling dari server!",
            onClosedConnection = {},
        )
    }

    override suspend fun getFromBackup(
        backupName: String,
        kavlingKode: String
    ): Result<HargaKavlingModel?> {
        return suspendCoroutine { continuation ->
            val backupHargaKavlingRef = FirebaseNodes
                .getBackupNode(databaseReference, backupName, FirebaseNodes.HARGA_KAVLING)
                .child(kavlingKode)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hargaKavlingModel = snapshot.getValue<HargaKavlingModel>()

                    continuation.resume(Result.success(hargaKavlingModel))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            }

            backupHargaKavlingRef.addListenerForSingleValueEvent(eventListener)
        }
    }


    override suspend fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Result<Nothing?> {
        return FirebaseRequestHelper.insertOperation(
            targetChild = hargaKavlingRef.child(hargaKavlingModel.kavlingKode),
            valueToInsert = hargaKavlingModel,
        )
    }

    override suspend fun deleteHargaKavlingModel(kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = hargaKavlingRef.child(kavlingKode),
        )
    }

    override fun getSingleHargaKavlingForPembayaran(
        kavlingKode: String,
        onSuccess: (harga: HargaKavlingModel?) -> Unit
    ) {
        hargaKavlingRef
            .child(kavlingKode)
            .get()
            .addOnSuccessListener { snapshot ->
                val hargaKavlingKavlingModel = snapshot.getValue<HargaKavlingModel>()

                onSuccess(hargaKavlingKavlingModel)
            }
    }

}