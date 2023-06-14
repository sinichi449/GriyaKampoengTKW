package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import net.bagusekasaputra.griyakampoengtkw.domain.misc.ChangesNotDetectedException
import net.bagusekasaputra.griyakampoengtkw.domain.misc.NotTheSameKeyIdException
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMaterialRepository

class UpdateBiayaMaterialAsyncUseCase(
    private val biayaMaterialRepository: BiayaMaterialRepository
): AsyncUseCase<UpdateBiayaMaterialAsyncUseCase.Request, Unit>() {

    data class Request(
        val oldBiayaMaterial: BiayaMaterial,
        val newBiayaMaterial: BiayaMaterial,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            val oldItem = request.oldBiayaMaterial
            val newItem = request.newBiayaMaterial

            if (oldItem.keyId != newItem.keyId) {
                emit(Result.failure(NotTheSameKeyIdException()))
            } else {
                if (oldItem.isEqualTo(newItem)) {
                    emit(Result.failure(ChangesNotDetectedException()))
                } else {
                    emit(biayaMaterialRepository.update(
                        keyId = oldItem.keyId,
                        newBiayaMaterial = newItem,
                    ))
                }
            }
        }
    }
}