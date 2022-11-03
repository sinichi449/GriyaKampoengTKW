package net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveKavlingUseCase @Inject constructor(
    private val kavlingRepository: KavlingRepository
): UseCase<RemoveKavlingUseCase.Request, RemoveKavlingUseCase.Response>() {

    data class Request(val blockKode: String, val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return kavlingRepository.removeKavling(request.blockKode, request.kavlingKode).map {
            Response(it)
        }
    }
}