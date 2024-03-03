package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class DeleteByIdTambahLuasanPembayaranAsyncUseCase(
    private val repository: PembayaranTambahLuasanRepository
): AsyncUseCase<DeleteByIdTambahLuasanPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val kavling: String,
        val id: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return repository.delete(request.kavling, request.id)
    }
}