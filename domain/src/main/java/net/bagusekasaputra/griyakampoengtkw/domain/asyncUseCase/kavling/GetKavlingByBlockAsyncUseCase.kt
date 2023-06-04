package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

class GetKavlingByBlockAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetKavlingByBlockAsyncUseCase.Request, List<Kavling>?>() {

    data class Request(val blockKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Kavling>?>> {
        return kavlingRepository.getKavlingByBlock(request.blockKode, request.dataMode).map { result ->
            // sort the kavling by number
            result.map { kavlingList ->
                if (kavlingList != null) {
                    val calendar = Calendar.getInstance()
                    val bulanIni = calendar.get(Calendar.MONTH) + 1
                    val tahunIni = calendar.get(Calendar.YEAR)

                    kavlingList.forEach {
                        val sudahBayarAngsuranBulanIni = pembayaranRepository
                            .sudahBayarAngsuran(it.kode, bulanIni, tahunIni, request.dataMode)
                            .getOrThrow()

                        it.sudahBayarBulanIni = sudahBayarAngsuranBulanIni ?: false
                    }

                    Kavling.sortKavling(kavlingList, SingleBlockKavlingSorter())
                } else {
                    // If kavlingList is null, just return the null value.
                    null
                }


            }
        }
    }
}