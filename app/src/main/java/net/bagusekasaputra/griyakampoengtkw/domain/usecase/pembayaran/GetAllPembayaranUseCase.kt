package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetSingleHargaKavlingForPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllPembayaranUseCase @Inject constructor(
    private val pembayaranRepository: PembayaranRepository,
    private val getSingleHargaKavlingForPembayaranUseCase: GetSingleHargaKavlingForPembayaranUseCase,
): UseCase<GetAllPembayaranUseCase.Request, GetAllPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<List<Pembayaran>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.getAllPembayaran(request.kavlingKode)
            .zip(getHargaKavling(request.kavlingKode)) { resultListPembayaran, hargaKavling ->
                val listPembayaran = resultListPembayaran.getOrNull()

                if (listPembayaran != null) {
                    val maskedPembayaran = maskPembayaran(listPembayaran, hargaKavling)

                    return@zip Result.success(maskedPembayaran)
                } else {
                    return@zip resultListPembayaran
                }
            }
            .map {
                Response(it)
            }
    }

    private fun maskPembayaran(
        listPembayaran: List<Pembayaran>,
        hargaKavling: Long
    ): List<Pembayaran> {
        val sortedListPembayaran = sortListPembayaran(listPembayaran)
        val newListPembayaran = ArrayList<Pembayaran>()
        var totalUangMasuk = 0L

        sortedListPembayaran.forEach {
            totalUangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            it.totalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
            it.presentase = getPersentase(totalUangMasuk, hargaKavling)
            Log.d(LOG_TAG, "Ready value pembayaran: $it")

            newListPembayaran.add(it)
        }

        return newListPembayaran
    }

    private fun getPersentase(totalUangMasuk: Long, hargaKavling: Long): Double {
        val floatTotalUangMasuk = totalUangMasuk.toFloat()
        val floatHargaKavling = hargaKavling.toFloat()
        val persentase = floatTotalUangMasuk.div(floatHargaKavling).let {
            val bigDecimal = it.toBigDecimal().setScale(4, RoundingMode.HALF_UP)
            return@let bigDecimal.multiply(BigDecimal.valueOf(100))
        }

        return persentase.toDouble()
    }

    private fun sortListPembayaran(listPembayaran: List<Pembayaran>): List<Pembayaran> {
        return listPembayaran.sortedBy {
            getMilliFromTanggal(it.tanggal)
        }
    }

    private fun getMilliFromTanggal(tanggal: String): Long {
        val calendar = Calendar.getInstance()
        tanggal.split("/").let {
            val year = it[2].toInt()
            val month = it[1].toInt()
            val day = it[0].toInt()
            Log.d(LOG_TAG, "Tanggal: $day/$month/$year")
            calendar.set(year, month, day, 0, 0, 0)
        }

        return calendar.toInstant().toEpochMilli()
    }

    private fun getHargaKavling(kavlingKode: String): Flow<Long> {
        return flow {
            val request = GetSingleHargaKavlingForPembayaranUseCase.Request(kavlingKode)

            getSingleHargaKavlingForPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.harga

                emit(result)
            }
        }
            .flowOn(Dispatchers.IO)
    }
}