package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddPembayaranUseCase @Inject constructor(
    private val pembayaranRepository: PembayaranRepository
): UseCase<AddPembayaranUseCase.Request, AddPembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String, val pembayaran: Pembayaran): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.addPembayaran(request.kavlingKode, request.pembayaran).map {
            Response(it)
        }
    }
}