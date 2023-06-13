package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengembalian

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.addIfNotNull
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.getOrEmitFailure
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository

/**
 * Stream [List] of [Pengembalian] entity sequentially.
 *
 * Don't use this use case with [first] method, since it will return a single [List].
 */
class GetPengembalianStreamAsyncUseCase(
    private val pengembalianRepository: PengembalianRepository,
): AsyncUseCase<GetPengembalianStreamAsyncUseCase.Request, List<Pengembalian>>() {

    /**
     * @param keyIds if left as an [emptyList], will retrieve all available keyIds from
     * [PengembalianRepository].
     * @param dataMode specify [DataMode] for this use case operation.
     */
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
                        .catch { emit(Result.failure(it)) }
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