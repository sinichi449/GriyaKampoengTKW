package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.materialPembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class DeleteMaterialPembangunanAsyncUseCase(
    private val materialPembangunanRepository: MaterialPembangunanRepository,
): AsyncUseCase<DeleteMaterialPembangunanAsyncUseCase.Request, Unit>() {

    data class Request(
        val keyId: String,
        val targetBangunan: String,
        val kategori: MaterialPembangunan.Kategori,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Unit?>> {
        return flow {
            var everythingsAlright = true

            if (request.keyId.isEmpty()) {
                everythingsAlright = false
                emit(Result.failure(IllegalArgumentException("Key Id tidak boleh kosong!")))
            }

            if (request.targetBangunan.isEmpty()) {
                everythingsAlright = false
                emit(Result.failure(IllegalArgumentException("Target Bangunan tidak boleh kosong!")))
            }

            if (everythingsAlright) {
                val identifier = MaterialPembangunan.Identifier(
                    kategori = request.kategori,
                    target = request.targetBangunan,
                    keyId = request.keyId
                )

                emit(materialPembangunanRepository.delete(identifier))
            }
        }
    }

}