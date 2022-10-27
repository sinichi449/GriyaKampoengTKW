package net.bagusekasaputra.griyakampung.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampung.domain.entity.Kavling
import net.bagusekasaputra.griyakampung.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetKavlingsByKodeUseCase @Inject constructor(
    private val repository: KavlingRepository
): UseCase<GetKavlingsByKodeUseCase.Request, GetKavlingsByKodeUseCase.Response>() {

    data class Request(val kode: String): UseCase.Request

    data class Response(val data: List<Kavling>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return repository.getKavlingByKode(request.kode).map {
            Response(it)
        }
    }
}