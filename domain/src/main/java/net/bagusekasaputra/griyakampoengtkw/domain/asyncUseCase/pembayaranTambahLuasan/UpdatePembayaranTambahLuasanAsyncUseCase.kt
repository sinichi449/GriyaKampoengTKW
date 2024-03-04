package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class UpdatePembayaranTambahLuasanAsyncUseCase(
    private val repository: PembayaranTambahLuasanRepository
): AsyncUseCase<UpdatePembayaranTambahLuasanAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val oldId: String,
        val newEntity: PembayaranTambahLuasan
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            // Ensure same ID
            if (request.oldId != request.newEntity.id) {
                emit(Result.failure(Exception("ID mismatched!")))
            } else {
                emitAll(repository.update(request.oldId, request.newEntity))
            }
        }
    }

}