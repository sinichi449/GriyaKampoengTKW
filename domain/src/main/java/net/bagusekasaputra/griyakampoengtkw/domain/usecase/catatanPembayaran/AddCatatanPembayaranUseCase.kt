package net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class AddCatatanPembayaranUseCase(
    private val catatanPembayaranRepository: CatatanPembayaranRepository,
): UseCase<AddCatatanPembayaranUseCase.Request, AddCatatanPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String, val catatanPembayaran: CatatanPembayaran): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return catatanPembayaranRepository.addCatatan(request.kavlingKode, request.catatanPembayaran).map {
            Response(it)
        }
    }
}