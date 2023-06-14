package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembangunan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.addIfNotNull
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMaterialRepository

class GetAllBiayaMaterialStreamAsyncUseCase(
    private val biayaMaterialRepository: BiayaMaterialRepository,
): AsyncUseCase<GetAllBiayaMaterialStreamAsyncUseCase.Request, List<BiayaMaterial>?>() {

    data class Request(val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<BiayaMaterial>?>> {
        return flow {
            val resultList = mutableListOf<BiayaMaterial>()
            biayaMaterialRepository.getAsFlow(request.dataMode).collect { result ->
                result.onFailure { emit(Result.failure(it)) }
                result.onSuccess { biayaMaterial ->
                    resultList.addIfNotNull(biayaMaterial)

                    emit(Result.success(
                        resultList.sortedBy { it.tanggal.time }
                            .ifEmpty { null }
                        )
                    )
                }
            }
        }
    }

}