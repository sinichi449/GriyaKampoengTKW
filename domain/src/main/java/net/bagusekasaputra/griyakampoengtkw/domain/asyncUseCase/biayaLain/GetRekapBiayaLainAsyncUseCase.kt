package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain.Companion.filterPeriode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository
import java.util.*

class GetRekapBiayaLainAsyncUseCase(
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<GetRekapBiayaLainAsyncUseCase.Request, List<BiayaLain>>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date?,
        val endDate: Date?,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<BiayaLain>?>> {
        return flow {
            val listBiayaLain = biayaLainRepository.getAllOffline()
                .first()
                .getOrThrow()

            when (request.periode) {
                PeriodeRekap.SEMUA -> emit(Result.success(listBiayaLain))
                PeriodeRekap.CUSTOM -> emit(Result.success(listBiayaLain?.filterPeriode(PeriodeRekap.CUSTOM, request.startDate, request.endDate)))
                else -> emit(Result.success(listBiayaLain?.filterPeriode(request.periode, null, null)))
            }
        }
    }
}