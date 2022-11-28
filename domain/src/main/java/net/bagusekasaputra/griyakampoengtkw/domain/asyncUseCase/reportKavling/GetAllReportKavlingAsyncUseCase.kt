package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressState
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ReportKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.math.BigDecimal
import java.math.RoundingMode

class GetAllReportKavlingAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
): AsyncUseCase<GetAllReportKavlingAsyncUseCase.Request, List<ReportKavling>?>() {

    object Request: AsyncUseCase.Request

    private val _progressStateLive = MutableLiveData<ProgressState>()
    val progressStateLive: LiveData<ProgressState>
        get() = _progressStateLive

    override fun process(request: Request): Flow<Result<List<ReportKavling>?>> {
        return flow {
            val kavlingList = getKavlingList()
            val listReportKavling = mutableListOf<ReportKavling>()

            val totalUnit = BigDecimal(kavlingList.size)

            kavlingList.forEachIndexed { index, kavlingKode ->
                val numProcessed = BigDecimal(index.plus(1))

                val progress = (numProcessed.divide(totalUnit, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100)))
                    .toInt()

                _progressStateLive.postValue(ProgressState(progress, "Memeriksa kavling $kavlingKode ... "))

                _progressStateLive.postValue(ProgressState(progress, "Memuat pembayaran pada kavling $kavlingKode ..."))
                val listPembayaran = pembayaranRepository.getAllOnline(kavlingKode)
                    .first()
                    .getOrThrow()

                _progressStateLive.postValue(ProgressState(progress, "Memuat fee marketing pada kavling $kavlingKode ..."))
                val feeMarketing = feeMarketingRepository.getAllOnline(kavlingKode)
                    .first()
                    .getOrThrow()

                _progressStateLive.postValue(ProgressState(progress, "Memuat biaya marketing pada kavling $kavlingKode ..."))
                val listBiayaMarketing = biayaMarketingRepository.getAllOnline(kavlingKode)
                    .first()
                    .getOrThrow()

                listReportKavling.add(
                    ReportKavling(
                        kavling = kavlingKode,
                        listPembayaran = listPembayaran,
                        feeMarketing = feeMarketing,
                        listBiayaMarketing = listBiayaMarketing,
                    )
                )
            }

            emit(Result.success(listReportKavling))
        }
    }

    private fun getKavlingList(): List<String> {
        val blockWithSum = mapOf(
            Pair("A", 14),
            Pair("B", 20),
            Pair("C", 9),
        )

        val listKavling = mutableListOf<String>()
        blockWithSum.keys.forEach { block ->
            val totalUnit = blockWithSum[block] ?: 0

            (1..totalUnit).forEach { noKavling ->
                listKavling.add("$block$noKavling")
            }
        }

        return listKavling
    }
}