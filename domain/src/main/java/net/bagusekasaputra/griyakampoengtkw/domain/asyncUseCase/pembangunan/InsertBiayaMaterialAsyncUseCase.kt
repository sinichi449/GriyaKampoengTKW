package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMaterialRepository

class InsertBiayaMaterialAsyncUseCase(
    private val biayaMaterialRepository: BiayaMaterialRepository
): AsyncUseCase<InsertBiayaMaterialAsyncUseCase.Request, Unit>() {

    data class Request(val biayaMaterial: BiayaMaterial): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            emit(biayaMaterialRepository.insert(request.biayaMaterial))
        }
    }
}