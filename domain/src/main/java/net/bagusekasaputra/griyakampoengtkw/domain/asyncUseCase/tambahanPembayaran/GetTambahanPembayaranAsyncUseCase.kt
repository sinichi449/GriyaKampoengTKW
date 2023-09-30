package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class GetTambahanPembayaranAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository
) : AsyncUseCase<GetTambahanPembayaranAsyncUseCase.Request, List<TambahanPembayaran>>() {

    data class Request(
        val kavling: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<TambahanPembayaran>?>> {
        return flow {
            emit(tambahanPembayaranRepository.getAllByKavling(request.kavling))
        }
    }


}