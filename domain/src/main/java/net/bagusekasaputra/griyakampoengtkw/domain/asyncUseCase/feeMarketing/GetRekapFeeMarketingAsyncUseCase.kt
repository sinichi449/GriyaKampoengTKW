package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import java.util.*

class GetRekapFeeMarketingAsyncUseCase(
    private val feeMarketingRepository: FeeMarketingRepository
): AsyncUseCase<GetRekapFeeMarketingAsyncUseCase.Request, List<FeeMarketing>>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date?,
        val endDate: Date?,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<FeeMarketing>?>> {
        return flow {
            val listFeeMarketing = feeMarketingRepository.getBatchOffline(request.kavlingList)
                .first()
                .getOrThrow()

            when (request.periode) {
                PeriodeRekap.SEMUA -> emit(Result.success(listFeeMarketing))
                PeriodeRekap.CUSTOM -> {
                    val newList = mutableListOf<FeeMarketing>()

                    listFeeMarketing?.forEach {
                        it.filterPeriode(PeriodeRekap.CUSTOM, request.startDate, request.endDate)?.let { feeMarketing ->
                            newList.add(feeMarketing)
                        }
                    }

                    if (newList.isEmpty()) emit(Result.success(null))
                    else emit(Result.success(newList))
                }
                else -> {
                    val newList = mutableListOf<FeeMarketing>()

                    listFeeMarketing?.forEach {
                        it.filterPeriode(request.periode, null, null)?.let { feeMarketing ->
                            newList.add(feeMarketing)
                        }
                    }

                    if (newList.isEmpty()) emit(Result.success(null))
                    else emit(Result.success(newList))
                }
            }
        }
    }
}