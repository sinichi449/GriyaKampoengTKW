package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.processNextEventInCurrentThread
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class AddNewTambahLuasanPembayaranAsyncUseCase(
    private val repository: PembayaranTambahLuasanRepository
): AsyncUseCase<AddNewTambahLuasanPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val entity: PembayaranTambahLuasan): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return repository.add(request.entity)
    }

}