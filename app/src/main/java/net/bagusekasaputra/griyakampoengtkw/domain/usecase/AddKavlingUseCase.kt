package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddKavlingUseCase @Inject constructor(
    private val kavlingRepository: KavlingRepository
): UseCase<AddKavlingUseCase.Request, AddKavlingUseCase.Response>() {

    data class Request(val blockKode: String, val kavling: Kavling): UseCase.Request
    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return kavlingRepository.addKavling(request.blockKode, request.kavling).map {
            Response(it)
        }
    }
}