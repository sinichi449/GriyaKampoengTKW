package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository

class IsFotoPembayaranExistAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<IsFotoPembayaranExistAsyncUseCase.Request, Boolean>() {

    data class Request(val kavlingKode: String, val termin: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Boolean?>> {
        return fotoPembayaranRepository.isFotoPembayaranExist(request.kavlingKode, request.termin, request.dataMode)
    }
}