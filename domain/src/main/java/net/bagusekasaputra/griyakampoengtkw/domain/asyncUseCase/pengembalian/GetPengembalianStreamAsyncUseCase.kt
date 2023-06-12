package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengembalian

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.addIfNotNull
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.getOrEmitFailure
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository

class GetPengembalianStreamAsyncUseCase(
    private val pengembalianRepository: PengembalianRepository,
): AsyncUseCase<GetPengembalianStreamAsyncUseCase.Request, List<Pengembalian>>() {

    data class Request(
        val keyIds: List<String> = emptyList(),
        val dataMode: DataMode = DataMode.ONLINE,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Pengembalian>?>> {
        return flow {
            val keyIds = request.keyIds.ifEmpty {
                pengembalianRepository.getKeyIds(request.dataMode)
                    .getOrEmitFailure(this)
            }

            if (keyIds.isNullOrEmpty()) {
                emit(Result.success(null))
            } else {
                val results = mutableListOf<Pengembalian>()

                keyIds.forEach { keyId ->
                    pengembalianRepository.getAsFlow(keyId, request.dataMode)
                        .collect { result ->
                            result.onFailure { emit(Result.failure(it)) }

                            result.onSuccess {
                                results.addIfNotNull(it)

                                emit(Result.success(results))
                            }
                        }
                }
            }
        }
    }
}