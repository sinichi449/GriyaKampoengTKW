package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class UpdateBiayaLainAsyncUseCase(
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<UpdateBiayaLainAsyncUseCase.Request, Nothing?>() {

    data class Request(val oldBiayaLain: BiayaLain, val newBiayaLain: BiayaLain): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return biayaLainRepository.updateBiayaLain(request.oldBiayaLain, request.newBiayaLain)
    }
}