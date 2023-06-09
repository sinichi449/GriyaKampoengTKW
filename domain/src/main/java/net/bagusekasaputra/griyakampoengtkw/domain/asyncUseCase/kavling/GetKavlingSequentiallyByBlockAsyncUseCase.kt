package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class GetKavlingSequentiallyByBlockAsyncUseCase(
    private val kavlingRepository: KavlingRepository
): AsyncUseCase<GetKavlingSequentiallyByBlockAsyncUseCase.Request, Kavling?>() {

    data class Request(val blok: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Kavling?>> {
        TODO("Not yet implemented")
    }
}