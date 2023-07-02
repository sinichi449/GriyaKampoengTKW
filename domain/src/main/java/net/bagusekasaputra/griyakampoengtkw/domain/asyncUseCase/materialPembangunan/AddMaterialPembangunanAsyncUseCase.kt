package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class AddMaterialPembangunanAsyncUseCase(
    private val materialPembangunanRepository: MaterialPembangunanRepository,
): AsyncUseCase<AddMaterialPembangunanAsyncUseCase.Request, Unit>() {

    data class Request(val materialPembangunan: MaterialPembangunan): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            val materialPembangunan = request.materialPembangunan

            materialPembangunan.validate()

            emit(materialPembangunanRepository.insert(
                kavling = materialPembangunan.untuk,
                materialPembangunan = materialPembangunan,
            ))
        }
    }
}