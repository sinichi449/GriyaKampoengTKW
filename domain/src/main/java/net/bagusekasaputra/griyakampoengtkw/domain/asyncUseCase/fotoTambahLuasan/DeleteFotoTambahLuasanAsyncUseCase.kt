package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository

class DeleteFotoTambahLuasanAsyncUseCase(
    private val repository: FotoTambahLuasanRepository
): AsyncUseCase<DeleteFotoTambahLuasanAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val kavling: String,
        val id: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return repository.delete(request.kavling, request.id)
    }
}