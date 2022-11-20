package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class GetKavlingByBlockAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
): AsyncUseCase<GetKavlingByBlockAsyncUseCase.Request, List<Kavling>?>() {

    data class Request(val blockKode: String, val offline: Boolean): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Kavling>?>> {
        return kavlingRepository.getKavlingByBlock(request.blockKode, request.offline).map { result ->
            // sort the kavling by number
            result.map { kavlingList ->
                if (kavlingList != null)
                    sortKavling(kavlingList)
                else
                    // If kavlingList is null, just return the null value.
                    null
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