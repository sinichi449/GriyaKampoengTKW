package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ProgressKavling

class GetSingleProgressKavlingAsyncUseCase(

): AsyncUseCase<GetSingleProgressKavlingAsyncUseCase.Request, ProgressKavling>() {

    data class Request(val kavling: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<ProgressKavling?>> {
        TODO("Not yet implemented")
    }
}