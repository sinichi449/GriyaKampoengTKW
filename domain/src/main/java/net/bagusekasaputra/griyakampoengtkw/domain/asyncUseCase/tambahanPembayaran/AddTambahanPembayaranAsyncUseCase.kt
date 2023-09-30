package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class AddTambahanPembayaranAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository,
): AsyncUseCase<AddTambahanPembayaranAsyncUseCase.Request, Nothing>() {

    data class Request(val tambahanPembayaran: TambahanPembayaran): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(tambahanPembayaranRepository.insert(request.tambahanPembayaran))
        }
    }
}