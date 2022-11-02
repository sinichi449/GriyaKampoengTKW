package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditKavlingUseCase @Inject constructor(
    private val kavlingRepository: KavlingRepository
): UseCase<EditKavlingUseCase.Request, EditKavlingUseCase.Response>() {

    data class Request(
        val blockKode: String,
        val oldKavling: Kavling,
        val newKavling: Kavling,): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return kavlingRepository.editKavling(request.blockKode,
            request.oldKavling, request.newKavling).map {
            Response(it)
        }
    }
}