package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class GetAllMaterialPembangunanAsyncUseCase(
    private val materialPembangunanRepository: MaterialPembangunanRepository,
): AsyncUseCase<GetAllMaterialPembangunanAsyncUseCase.Request, List<MaterialPembangunan>?>() {

    data class Request(
        val kavling: String,
        val dataMode: DataMode
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<MaterialPembangunan>?>> {
        return materialPembangunanRepository.getAll(request.kavling, request.dataMode)
    }

}