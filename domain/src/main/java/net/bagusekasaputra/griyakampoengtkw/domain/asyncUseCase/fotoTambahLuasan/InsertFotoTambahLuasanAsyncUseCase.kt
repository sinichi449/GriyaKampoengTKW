package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository

class InsertFotoTambahLuasanAsyncUseCase(
    private val repository: FotoTambahLuasanRepository
): AsyncUseCase<InsertFotoTambahLuasanAsyncUseCase.Request, Nothing?>() {

    data class Request(val entity: FotoTambahLuasan): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return repository.insert(request.entity)
    }
}