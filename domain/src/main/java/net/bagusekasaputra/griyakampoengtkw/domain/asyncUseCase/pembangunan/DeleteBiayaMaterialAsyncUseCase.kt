package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMaterialRepository

class DeleteBiayaMaterialAsyncUseCase(
    private val biayaMaterialRepository: BiayaMaterialRepository,
): AsyncUseCase<DeleteBiayaMaterialAsyncUseCase.Request, Unit>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            emit(biayaMaterialRepository.delete(request.keyId))
        }
    }
}