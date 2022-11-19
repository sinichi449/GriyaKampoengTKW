package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteFotoPembayaranAsyncUseCase @Inject constructor(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<DeleteFotoPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavlingKode: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return fotoPembayaranRepository.deleteFotoPembayaran(request.kavlingKode, request.termin)
    }
}