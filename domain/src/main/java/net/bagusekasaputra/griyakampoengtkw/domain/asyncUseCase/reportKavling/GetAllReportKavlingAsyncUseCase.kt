package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ReportKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetAllReportKavlingAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
): AsyncUseCase<GetAllReportKavlingAsyncUseCase.Request, List<ReportKavling>?>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<ReportKavling>?>> {
        return flow {
            val kavlingList = getKavlingList()
            val listReportKavling = mutableListOf<ReportKavling>()

            kavlingList.forEach { kavlingKode ->
                val listPembayaran = pembayaranRepository.getAllOnline(kavlingKode)
                    .first()
                    .getOrThrow()
                val feeMarketing = feeMarketingRepository.getAllOnline(kavlingKode)
                    .first()
                    .getOrThrow()
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