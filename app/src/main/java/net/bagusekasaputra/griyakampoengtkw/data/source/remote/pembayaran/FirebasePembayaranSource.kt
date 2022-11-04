package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePembayaranSource @Inject constructor(
    private val databaseReference: DatabaseReference
): RemotePembayaranSource {

    init {
        databaseReference.child(GriyaNodes.formPembayaran).keepSynced(true)
    }

    override fun addPembayaranModel(
        kavlingKode: String,
        pembayaranModel: PembayaranModel,
    ): Flow<Result<Boolean>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.formPembayaran)
                .child(kavlingKode)
                .child(pembayaranModel.termin)
                .setValue(pembayaranModel)
                .addOnSuccessListener {
                    trySendBlocking(Result.success(true))
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun getLatestTotalUangMasuk(kavlingKode: String): Flow<Result<Long>> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.formPembayaran)
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val terminHashmap = snapshot.getValue<HashMap<String, PembayaranModel>>()
                    val mostRecentDate = getMostRecentDate(terminHashmap)
                    Log.d(LOG_TAG, "Most recent date: $mostRecentDate")

                    // traverse through hashmap for specific date
                    terminHashmap?.keys?.let { keys ->
                        for (key in keys) {
                            if (terminHashmap[key]!!.tanggal == mostRecentDate) {
                                val latestUangMasuk = terminHashmap[key]!!.totalUangMasuk
                                trySendBlocking(Result.success(latestUangMasuk))

                                break
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose { }
        }
    }

    private fun getMostRecentDate(terminHashmap: HashMap<String, PembayaranModel>?): String {
        val dateList = ArrayList<Date>()
        terminHashmap?.keys?.forEach { termin ->
            val tanggal = terminHashmap[termin]?.tanggal
            tanggal?.let {
                dateList.add(getDateFromString(it))
            }
        }

        dateList.sortByDescending { it.toInstant().toEpochMilli() }

        return SimpleDateFormat("dd/MM/yyyy").format(dateList[0])
    }

    private fun getDateFromString(tanggal: String): Date {
        val arrTanggal = tanggal.split("/")
        val day = arrTanggal[0].toInt()
        val month = arrTanggal[1].toInt() - 1
        val year = arrTanggal[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)

        return calendar.time
    }
}