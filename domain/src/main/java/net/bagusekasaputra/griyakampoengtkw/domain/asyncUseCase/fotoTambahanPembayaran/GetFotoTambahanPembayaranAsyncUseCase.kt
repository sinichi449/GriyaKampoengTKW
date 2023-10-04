package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository

class GetFotoTambahanPembayaranAsyncUseCase(
    private val repo: FotoTambahanPembayaranRepository,
): AsyncUseCase<GetFotoTambahanPembayaranAsyncUseCase.Request, FotoTambahanPembayaran>() {

    data class Request(
        val kavling: String,
        val id: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<FotoTambahanPembayaran?>> {
        return flow {
            emit(repo.get(request.kavling, request.id))
        }
    }
}