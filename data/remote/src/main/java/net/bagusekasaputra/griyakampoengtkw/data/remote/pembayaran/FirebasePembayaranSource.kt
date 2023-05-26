package net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseRequestHelper
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebasePembayaranSource(
    private val databaseReference: DatabaseReference,
    private val pembayaranIndenBookingDataSource: RemotePembayaranIndenBookingDataSource,
): RemotePembayaranSource {

    private val pembayaranRef = databaseReference.child(FirebaseNodes.FORM_PEMBAYARAN)

    override suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?> {
        return FirebaseRequestHelper.getOperation(
            pathToChild = pembayaranRef.child(kavlingKode),
            onGetSnapshot = { snapshot ->
                val terminHashMap = snapshot.getValue<HashMap<String, PembayaranModel>>()

                if (terminHashMap != null) {
                    val listPembayaranModel = ArrayList<PembayaranModel>()
                    for (key in terminHashMap.keys) {
                        terminHashMap[key]?.let {
                            listPembayaranModel.add(it)
                        }
                    }
                    return@getOperation listPembayaranModel
                } else {
                    return@getOperation null
                }
            },
            timeOutMsg = "Waktu habis mendapatkan data pembayaran",
            onClosedConnection = {},
        )
    }

    override suspend fun getAllFromBackup(
        backupName: String,
        kavlingKode: String
    ): Result<List<PembayaranModel>?> {
        return suspendCoroutine { continuation ->
            val backupPembayaranRef = FirebaseNodes
                .getBackupNode(databaseReference, backupName, FirebaseNodes.FORM_PEMBAYARAN)
                .child(kavlingKode)
            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val terminMap = snapshot.getValue<HashMap<String, PembayaranModel>>()
                    val listPembayaran = mutableListOf<PembayaranModel>()

                    terminMap?.keys?.forEach {
                        val pembayaran = terminMap[it]
                        if (pembayaran != null) {
                            listPembayaran.add(pembayaran)
                        }
                    }

                    continuation.resume(Result.success(listPembayaran))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            }

            backupPembayaranRef.addListenerForSingleValueEvent(eventListener)
        }
    }

    override suspend fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        val terminChild = pembayaranModel.getFullTermin()

        // Check if child is available to avoid replacing the available data.
        // if the user intended to replace, he must go through edit.
        return FirebaseRequestHelper.insertOperationAlertOverwrite(
            targetChild = pembayaranRef
                .child(kavlingKode)
                .child(terminChild),
            valueToInsert = pembayaranModel,
            existMsg = "Termin sudah ada!",
        )
    }

    override suspend fun updatePembayaranModel(
        kavlingKode: String,
        oldPembayaranModel: PembayaranModel,
        newPembayaranModel: PembayaranModel
    ): Result<Nothing?> {
        val terminChild = getTerminChild(oldPembayaranModel.termin, oldPembayaranModel.urutan)

        // This helper automatically remove the existing data before adding the new one.
        return FirebaseRequestHelper.updateOperation(
            targetChild = pembayaranRef
                .child(kavlingKode)
                .child(terminChild),
            newValue = newPembayaranModel,
        )
    }

    override suspend fun deletePembayaranModelByTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = pembayaranRef.child(kavlingKode).child(termin)
        )
    }

    override suspend fun deleteAllPembayaranModel(kavlingKode: String): Result<Nothing?> {
        return FirebaseRequestHelper.deleteOperation(
            targetChild = pembayaranRef.child(kavlingKode),
        )
    }


    /**
     * Inden Booking related
     */
    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<PembayaranModel>?> {
        return pembayaranIndenBookingDataSource.getAll(keyId)
    }


    private fun isTerminChildAvailable(
        kavlingKode: String,
        terminChild: String,
        onSuccess: (available: Boolean) -> Unit,
    ) {
        pembayaranRef
            .child(kavlingKode)
            .child(terminChild)
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.getValue<PembayaranModel>()

                if (data == null) onSuccess(false)
                else onSuccess(true)
            }
    }

    private fun getTerminChild(termin: String, urutan: Int): String {
        return "$termin $urutan"
    }

}

/**
 * This interface will prevent dependency to Inden Booking counterpart, since it is very unstable,
 * and instead inverting that relation.
 *
 * (Dependency Inversion?)
 */
interface RemotePembayaranIndenBookingDataSource {

    suspend fun getAll(keyId: String): Result<List<PembayaranModel>?>

}