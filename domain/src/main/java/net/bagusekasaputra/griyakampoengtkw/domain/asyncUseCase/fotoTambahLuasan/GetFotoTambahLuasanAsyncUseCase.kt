package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahLuasan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository

class GetFotoTambahLuasanAsyncUseCase(
    private val repository: FotoTambahLuasanRepository,
): AsyncUseCase<GetFotoTambahLuasanAsyncUseCase.Request, FotoTambahLuasan?>() {

    data class Request(
        val kavling: String,
        val tambahLuasanId: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<FotoTambahLuasan?>> {
        return repository.get(request.kavling, request.tambahLuasanId)
    }
}