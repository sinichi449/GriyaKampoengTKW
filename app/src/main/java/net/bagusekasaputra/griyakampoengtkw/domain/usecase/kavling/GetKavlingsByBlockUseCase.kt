package net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetKavlingsByBlockUseCase @Inject constructor(
    private val repository: KavlingRepository
): UseCase<GetKavlingsByBlockUseCase.Request, GetKavlingsByBlockUseCase.Response>() {

    data class Request(val blockKode: String): UseCase.Request

    data class Response(val result: Result<List<Kavling>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return repository.getKavlingByBlock(request.blockKode).map { result ->
            Response(
                // sort the kavling by number
                result = result.map { kavlingList ->
                    if (kavlingList != null)
                        sortKavling(kavlingList)
                    else
                        kavlingList
                }
            )
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