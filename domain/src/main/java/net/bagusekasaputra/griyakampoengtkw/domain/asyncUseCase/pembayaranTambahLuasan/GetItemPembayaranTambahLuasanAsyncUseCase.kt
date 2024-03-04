package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class GetItemPembayaranTambahLuasanAsyncUseCase(
    private val repository: PembayaranTambahLuasanRepository
): AsyncUseCase<GetItemPembayaranTambahLuasanAsyncUseCase.Request, PembayaranTambahLuasan?>() {

    data class Request(
        val kavling: String,
        val id: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<PembayaranTambahLuasan?>> {
        return repository.get(request.kavling, request.id)
    }
}