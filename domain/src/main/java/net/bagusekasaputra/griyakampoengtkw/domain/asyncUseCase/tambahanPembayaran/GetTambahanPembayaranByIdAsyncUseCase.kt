package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class GetTambahanPembayaranByIdAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository
): AsyncUseCase<GetTambahanPembayaranByIdAsyncUseCase.Request, TambahanPembayaran>() {

    data class Request(
        val kavling: String,
        val id: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<TambahanPembayaran?>> {
        return flow {
            emit(tambahanPembayaranRepository.getById(request.kavling, request.id))
        }
    }
}