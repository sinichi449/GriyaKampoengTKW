package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.*

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
                    val bulanIni = Calendar.getInstance().let {
                        it.get(Calendar.MONTH).plus(1)
                    }

                    kavlingList.forEach {
                        val sudahBayarAngsuranBulanIni = pembayaranRepository
                            .sudahBayarAngsuran(it.kode, bulanIni, request.dataMode)
                            .getOrThrow()
                            ?: false
                        it.sudahBayarBulanIni = sudahBayarAngsuranBulanIni
                    }

                    sortKavling(kavlingList)
                } else {
                    // If kavlingList is null, just return the null value.
                    null
                }


            }
        }
    }

    private fun sortKavling(kavlings: List<Kavling>): List<Kavling> {
        // We need a mutable list first for sorting the kavlings
        val mutableKavling = mutableListOf<Kavling>()

        kavlings.forEach {
            mutableKavling.add(it)
        }

        mutableKavling.sortBy {
            it.kode.substring(1).toInt()
        }

        return mutableKavling
    }
}