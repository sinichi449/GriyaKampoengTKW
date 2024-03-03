package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class GetAllPembayaranTambahLuasanAsyncUseCase(
    private val pembayaranTambahLuasanRepository: PembayaranTambahLuasanRepository
): AsyncUseCase<GetAllPembayaranTambahLuasanAsyncUseCase.Request, List<PembayaranTambahLuasan>?>() {

    data class Request(val kavling: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<PembayaranTambahLuasan>?>> {
        return pembayaranTambahLuasanRepository.getAll(request.kavling).map { result ->
            result.map { data ->
                data?.let {
                    PembayaranTambahLuasan.sortByTanggal(it)
                }
            }
        }
    }


}