package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.SingleBlockKavlingSorter
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class GetKavlingByBlockAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
): AsyncUseCase<GetKavlingByBlockAsyncUseCase.Request, List<Kavling>?>() {

    data class Request(val blockKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Kavling>?>> {
        return kavlingRepository.getKavlingByBlock(request.blockKode, request.dataMode).map { result ->
            // sort the kavling by number
            result.map { kavlingList ->
                if (!kavlingList.isNullOrEmpty()) {
                    Kavling.sortKavling(kavlingList, SingleBlockKavlingSorter())
                } else {
                    // If kavlingList is null, just return the null value.
                    null
                }
            }
        }
    }
}