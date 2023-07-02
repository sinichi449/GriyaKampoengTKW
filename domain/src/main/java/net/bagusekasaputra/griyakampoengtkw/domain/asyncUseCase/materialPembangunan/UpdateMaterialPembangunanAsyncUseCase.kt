package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class UpdateMaterialPembangunanAsyncUseCase(
    private val materialPembangunanRepository: MaterialPembangunanRepository
): AsyncUseCase<UpdateMaterialPembangunanAsyncUseCase.Request, Unit>() {

    data class Request(
        val oldData: MaterialPembangunan,
        val newData: MaterialPembangunan,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            var everythingsAlright = true

            if (request.oldData == request.newData) {
                everythingsAlright = false
                emit(Result.failure(IllegalStateException("Tidak ada perubahan pada data!")))
            }

            if (request.oldData.getIdentifier() != request.newData.getIdentifier()) {
                everythingsAlright = false
                emit(Result.failure(IllegalStateException("Identifier Material Pembangunan terdeteksi tidak sama!")))
            }

            if (everythingsAlright) {
                emit(materialPembangunanRepository.update(
                    identifier = request.newData.getIdentifier(),
                    newData = request.newData,
                ))
            }
        }
    }
}