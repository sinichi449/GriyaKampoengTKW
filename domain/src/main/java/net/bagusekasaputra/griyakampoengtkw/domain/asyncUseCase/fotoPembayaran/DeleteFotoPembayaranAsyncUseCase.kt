package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StandardAmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository

class DeleteFotoPembayaranAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
    private val standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
): AsyncUseCase<DeleteFotoPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavlingKode: String, val termin: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return callbackFlow {
            fotoPembayaranRepository.deleteFotoPembayaran(request.kavlingKode, request.termin)
                .first()
                .onSuccess {
                    // Set ambil kuitansi to false
                    standardAmbilKuitansiRepository.insert(
                        StandardAmbilKuitansi(request.kavlingKode, request.termin, false)
                    )
                        .onSuccess {
                            trySendBlocking(Result.success(null))
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }
}