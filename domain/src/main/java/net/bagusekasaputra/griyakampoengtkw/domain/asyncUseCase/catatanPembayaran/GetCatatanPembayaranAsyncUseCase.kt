package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository

class GetCatatanPembayaranAsyncUseCase(
    private val catatanPembayaranRepository: CatatanPembayaranRepository,
): AsyncUseCase<GetCatatanPembayaranAsyncUseCase.Request, CatatanPembayaran?>() {

    data class Request(val kavlingKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<CatatanPembayaran?>> {
        return catatanPembayaranRepository.getCatatan(request.kavlingKode, request.dataMode)
    }
}