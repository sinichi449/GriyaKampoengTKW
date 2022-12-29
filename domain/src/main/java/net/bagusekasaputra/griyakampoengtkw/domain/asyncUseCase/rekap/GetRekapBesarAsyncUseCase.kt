package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PeriodeRekap
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapBesar
import java.util.*

class GetRekapBesarAsyncUseCase(

): AsyncUseCase<GetRekapBesarAsyncUseCase.Request, RekapBesar>() {

    data class Request(
        val kavlingList: List<String>,
        val periode: PeriodeRekap,
        val startDate: Date?,
        val endDate: Date?,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<RekapBesar?>> {
        return when(request.periode) {
            PeriodeRekap.SEMUA -> getAllRekapBesar(request)
            PeriodeRekap.TAHUN_INI -> getTahunIni(request)
            PeriodeRekap.BULAN_INI -> getBulanIni(request)
            PeriodeRekap.MINGGU_INI -> getMingguIni(request)
            PeriodeRekap.CUSTOM -> getCustomRekap(request)
        }
    }

    private fun getAllRekapBesar(request: Request): Flow<Result<RekapBesar?>> {
        TODO()
    }

    private fun getTahunIni(request: Request): Flow<Result<RekapBesar?>> {
        TODO()
    }

    private fun getBulanIni(request: Request): Flow<Result<RekapBesar?>> {
        TODO()
    }

    private fun getMingguIni(request: Request): Flow<Result<RekapBesar?>> {
        TODO()
    }

    private fun getCustomRekap(request: Request): Flow<Result<RekapBesar?>> {
        TODO()
    }
}