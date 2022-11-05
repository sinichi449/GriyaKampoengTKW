package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePembayaranUseCase @Inject constructor(
    private val pembayaranRepository: PembayaranRepository
): UseCase<UpdatePembayaranUseCase.Request, UpdatePembayaranUseCase.Response>() {

    data class Request(val kavlingKode: String, val oldPembayaran: Pembayaran, val newPembayaran: Pembayaran): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.updatePembayaran(
            request.kavlingKode,
            request.oldPembayaran,
            request.newPembayaran
        ).map {
            Response(it)
        }
    }
}