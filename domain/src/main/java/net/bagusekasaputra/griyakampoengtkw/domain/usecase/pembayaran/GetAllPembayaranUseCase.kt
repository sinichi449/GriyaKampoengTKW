package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.PembayaranSorterUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetSingleHargaKavlingForPembayaranUseCase
import java.math.BigDecimal
import java.math.RoundingMode

class GetAllPembayaranUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val getSingleHargaKavlingForPembayaranUseCase: GetSingleHargaKavlingForPembayaranUseCase,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): UseCase<GetAllPembayaranUseCase.Request, GetAllPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<List<Pembayaran>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.getAllPembayaran(request.kavlingKode)
            .zip(getHargaKavling(request.kavlingKode)) { resultListPembayaran, hargaKavling ->
                val listPembayaran = resultListPembayaran.getOrNull()

                if (listPembayaran != null) {
                    /**
                     * We need to "mask" the pembayaran:
                     *
                     * 1. Sort the pembayaran according to ITJ, DP, and Termin order.
                     *
                     * 2. Calculate the "Total Uang Masuk".
                     *
                     * 3. Calculate the "presentase".
                     *
                     * 4. Calculate the "Sisa Belum Bayar".
                     *
                     * So... we need a "GetSingleHargaKavlingUseCase" to do the operation number 2 - 4.
                     */
                    val maskedPembayaran = maskPembayaran(listPembayaran, hargaKavling)

                    // Check sudah isi form pembayaran
                    maskedPembayaran.forEach { pembayaran ->
                        val sudahIsiFormPembayaran = checkSudahIsiFormPembayaran(
                            kavlingKode = request.kavlingKode,
                            termin = pembayaran.termin,
                        )

                        pembayaran.sudahIsiFotoPembayaran = sudahIsiFormPembayaran
                    }

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
        hargaKavling: HargaKavling,
    ): List<Pembayaran> {
        val sortedListPembayaran = sortListPembayaran(listPembayaran)
        val newListPembayaran = ArrayList<Pembayaran>()
        var totalUangMasuk = 0L

        sortedListPembayaran.forEach {
            totalUangMasuk += NumberUtil.formatStringToLong(it.jumlahUangDibayar)
            it.totalUangMasuk = NumberUtil.formatLongToString(totalUangMasuk)
            it.presentase = getPersentase(totalUangMasuk, hargaKavling)
            it.sisaBelumTerbayar = NumberUtil.formatLongToString(hargaKavling - totalUangMasuk)

            newListPembayaran.add(it)
        }

        return newListPembayaran
    }

    private fun getPersentase(totalUangMasuk: Long, hargaKavling: HargaKavling): Double {
        val floatTotalUangMasuk = totalUangMasuk.toFloat()
        val floatHargaKavling = hargaKavling.toFloat()
        val persentase = floatTotalUangMasuk.div(floatHargaKavling).let {
            val bigDecimal = it.toBigDecimal().setScale(4, RoundingMode.HALF_UP)
            return@let bigDecimal.multiply(BigDecimal.valueOf(100))
        }

        return persentase.toDouble()
    }

    private fun sortListPembayaran(listPembayaran: List<Pembayaran>): List<Pembayaran> {
        return PembayaranSorterUtil(listPembayaran).getSortedList()
    }

    private fun getHargaKavling(kavlingKode: String): Flow<HargaKavling> {
        return flow<HargaKavling> {
            val request = GetSingleHargaKavlingForPembayaranUseCase.Request(kavlingKode)

            getSingleHargaKavlingForPembayaranUseCase.execute(request).collect { response ->
                val result = response.data.hargaKavling

                emit(result)
            }
        }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun checkSudahIsiFormPembayaran(kavlingKode: String, termin: String): Boolean {
        return callbackFlow<Boolean> {
            fotoPembayaranRepository.isFotoPembayaranExist(
                kavlingKode = kavlingKode,
                termin = termin,
            ).collect { result ->
                result.onSuccess {
                    trySendBlocking(it)
                }
                result.onFailure {

                }
            }

            awaitClose {  }
        }.first()
    }
}