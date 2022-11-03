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
        return repository.getKavlingByBlock(request.blockKode).map {
            Response(it)
        }
    }
}