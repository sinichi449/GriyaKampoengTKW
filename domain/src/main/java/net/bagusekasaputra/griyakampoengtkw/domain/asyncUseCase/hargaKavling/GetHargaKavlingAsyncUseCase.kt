package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.hargaKavling

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository

class GetHargaKavlingAsyncUseCase(
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<GetHargaKavlingAsyncUseCase.Request, HargaKavling?>() {

    data class Request(val kavlingKode: String, val offline: Boolean): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<HargaKavling?>> {
        return hargaKavlingRepository.getHargaKavling(request.kavlingKode, request.offline)
    }
}