package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.UnmigratedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class GetListUnmigratedKavlingsAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
    private val dataDiriRepository: DataDiriRepository,
): AsyncUseCase<GetListUnmigratedKavlingsAsyncUseCase.Request, List<UnmigratedKavling>>() {

    data class Request(val backupName: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<UnmigratedKavling>?>> {
        return callbackFlow {
            try {
                val resultListKavling = kavlingRepository.getUnmigratedKavlings(request.backupName)
                    .first().getOrThrow()

                val listUnmigratedKavling = mutableListOf<UnmigratedKavling>()
                resultListKavling?.forEach { kavling ->
                    val dataDiriKavlingLama = dataDiriRepository.getDataDiriFromRemoteBackup(request.backupName, kavling)
                        .first().getOrThrow()

                    listUnmigratedKavling.add(
                        UnmigratedKavling(
                            kavlingKode = kavling,
                            namaCostumer = dataDiriKavlingLama?.nama ?: "NULL",
                        )
                    )
                }

                trySendBlocking(Result.success(listUnmigratedKavling))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}