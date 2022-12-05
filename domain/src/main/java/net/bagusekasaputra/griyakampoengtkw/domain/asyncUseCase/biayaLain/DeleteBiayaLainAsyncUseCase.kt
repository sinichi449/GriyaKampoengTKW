package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class DeleteBiayaLainAsyncUseCase(
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<DeleteBiayaLainAsyncUseCase.Request, Nothing?>() {

    data class Request(val oldBiayaLain: BiayaLain): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return biayaLainRepository.deleteBiayaLain(request.oldBiayaLain)
    }
}