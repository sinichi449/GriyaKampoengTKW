package net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddHargaKavlingUseCase @Inject constructor(
    private val hargaKavlingRepository: HargaKavlingRepository
): UseCase<AddHargaKavlingUseCase.Request, AddHargaKavlingUseCase.Response>() {

    data class Request(val hargaKavling: HargaKavling): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return hargaKavlingRepository.addHargaKavling(request.hargaKavling).map {
            Response(it)
        }
    }
}