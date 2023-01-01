package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import java.util.*

class GetRekapBiayaMarketingAsyncUseCase(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): AsyncUseCase<GetRekapBiayaMarketingAsyncUseCase.Request, Map<String, List<BiayaMarketing>>>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date?,
        val endDate: Date?,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Map<String, List<BiayaMarketing>>?>> {
        return flow {
            val batchBiayaMarketing = biayaMarketingRepository.getBatchOffline(request.kavlingList)
                .first()
                .getOrThrow()

            when (request.periode) {
                PeriodeRekap.SEMUA -> emit(Result.success(batchBiayaMarketing))
                PeriodeRekap.CUSTOM -> {
                    val newBatch = mutableMapOf<String, List<BiayaMarketing>>()

                    batchBiayaMarketing?.forEach { item ->
                        item.value.filterPeriode(PeriodeRekap.CUSTOM, request.startDate, request.endDate)?.let { filtered ->
                            newBatch[item.key] = filtered
                        }
                    }

                    if (newBatch.isNotEmpty()) emit(Result.success(newBatch))
                    else emit(Result.success(null))
                }
                else -> {
                    val newBatch = mutableMapOf<String, List<BiayaMarketing>>()

                    batchBiayaMarketing?.forEach { item ->
                        item.value.filterPeriode(request.periode, null, null)?.let { filtered ->
                            newBatch[item.key] = filtered
                        }
                    }

                    if (newBatch.isNotEmpty()) emit(Result.success(newBatch))
                    else emit(Result.success(null))
                }
            }
        }
    }

}