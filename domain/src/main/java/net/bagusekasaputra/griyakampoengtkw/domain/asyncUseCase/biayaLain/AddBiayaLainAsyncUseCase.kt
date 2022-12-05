package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class AddBiayaLainAsyncUseCase(
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<AddBiayaLainAsyncUseCase.Request, Nothing?>() {

    data class Request(val biayaLain: BiayaLain): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return biayaLainRepository.addBiayaLain(request.biayaLain)
    }
}