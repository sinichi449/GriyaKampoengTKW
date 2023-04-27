package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository

class GetRekapBesarDetailAsyncUseCase(
    private val rekapBesarDetailRepository: RekapBesarDetailRepository
): AsyncUseCase<GetRekapBesarDetailAsyncUseCase.Request, RekapBesarDetail>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<RekapBesarDetail?>> {
        return callbackFlow {
            trySendBlocking(rekapBesarDetailRepository.get())

            awaitClose { }
        }
    }
}