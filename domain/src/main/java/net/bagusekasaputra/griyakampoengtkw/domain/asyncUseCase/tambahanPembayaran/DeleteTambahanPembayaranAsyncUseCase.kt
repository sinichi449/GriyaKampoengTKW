package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class DeleteTambahanPembayaranAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository,
): AsyncUseCase<DeleteTambahanPembayaranAsyncUseCase.Request, Nothing>() {

    data class Request(
        val kavling: String,
        val id: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(tambahanPembayaranRepository.delete(request.kavling, request.id))
        }
    }
}