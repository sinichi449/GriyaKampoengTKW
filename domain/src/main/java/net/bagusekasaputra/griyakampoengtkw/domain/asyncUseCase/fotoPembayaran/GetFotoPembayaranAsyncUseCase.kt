package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository

class GetFotoPembayaranAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<GetFotoPembayaranAsyncUseCase.Request, FotoPembayaran>() {

    data class Request(val kavlingKode: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<FotoPembayaran?>> {
        return fotoPembayaranRepository.getFotoPembayaran(request.kavlingKode, request.termin)
    }
}