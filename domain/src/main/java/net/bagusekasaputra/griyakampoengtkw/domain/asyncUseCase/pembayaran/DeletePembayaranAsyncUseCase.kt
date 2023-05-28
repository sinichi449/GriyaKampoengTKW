package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class DeletePembayaranAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
) : AsyncUseCase<DeletePembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavling: String, val pembayaran: Pembayaran): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return callbackFlow {
            // Delete data pembayaran
            pembayaranRepository.deletePembayaranByTermin(
                kavlingKode = request.kavling,
                termin = request.pembayaran.termin,
            ).first()
                .onSuccess {
                    // Delete foto pembayaran, if exist
                    if (request.pembayaran.sudahIsiFotoPembayaran) {
                        fotoPembayaranRepository.deleteFotoPembayaran(
                            kavlingKode = request.kavling,
                            termin = request.pembayaran.termin,
                        ).first()
                            .onSuccess {
                                trySendBlocking(Result.success(null))
                            }
                            .onFailure {
                                trySendBlocking(Result.failure(it))
                            }
                    } else {
                        trySendBlocking(Result.success(null))
                    }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }
}