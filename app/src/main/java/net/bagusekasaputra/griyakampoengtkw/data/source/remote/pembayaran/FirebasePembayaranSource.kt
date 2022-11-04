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

    override fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel
    ): Flow<Result<Boolean>> {
        Log.d(LOG_TAG, "Adding pembayaran on $kavlingKode")
        return callbackFlow {
            getAllTotalUangMasuk(kavlingKode).collect { allTotalUangMasuk ->
                Log.d(LOG_TAG, "All total uang masuk $allTotalUangMasuk")
                if (allTotalUangMasuk <= 0L) {
                    pembayaranModel.totalUangMasuk = pembayaranModel.jumlahUangDibayar
                } else {
                    pembayaranModel.totalUangMasuk = allTotalUangMasuk + pembayaranModel.jumlahUangDibayar
                }
                pembayaranModel.presentase = pembayaranModel.totalUangMasuk.toDouble() / hargaKavling.toDouble()

                Log.d(LOG_TAG, "Presentase: ${pembayaranModel.presentase}")

                databaseReference
                    .child(GriyaNodes.formPembayaran)
                    .child(kavlingKode)
                    .child(pembayaranModel.termin)
                    .setValue(pembayaranModel)
                    .addOnSuccessListener {
                        Log.d(LOG_TAG, "Successfully add pembayaran")
                        this.trySendBlocking(Result.success(true))
                    }
                    .addOnFailureListener {
                        Log.d(LOG_TAG, "Fail to add pembayaran: ${it.message}")
                        this.trySendBlocking(Result.failure(it))
                    }
            }

            awaitClose {  }
        }
    }

    private fun getAllTotalUangMasuk(kavlingKode: String): Flow<Long> {
        return callbackFlow {
            databaseReference
                .child(GriyaNodes.formPembayaran)
                .child(kavlingKode)
                .get()
                .addOnSuccessListener { snapshot ->
                    val terminsHashmap = snapshot.getValue<HashMap<String, PembayaranModel>>()
                    var allTotalUangMasuk = 0L

                    if (terminsHashmap != null) {
                        for (key in terminsHashmap.keys) {
                            terminsHashmap[key]?.totalUangMasuk?.let {
                                Log.d(LOG_TAG, "Found totalMasuk: $it")
                                allTotalUangMasuk += it
                            }
                        }

                        trySendBlocking(allTotalUangMasuk)
                    }
                }

            awaitClose {  }
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