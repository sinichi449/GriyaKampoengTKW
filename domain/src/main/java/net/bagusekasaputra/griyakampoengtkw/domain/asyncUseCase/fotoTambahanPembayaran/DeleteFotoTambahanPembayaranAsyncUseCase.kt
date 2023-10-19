package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository

class DeleteFotoTambahanPembayaranAsyncUseCase(
    private val repository: FotoTambahanPembayaranRepository,
): AsyncUseCase<DeleteFotoTambahanPembayaranAsyncUseCase.Request, Nothing>() {

    data class Request(
        val kavling: String,
        val id: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(repository.delete(request.kavling, request.id))
        }
    }
}