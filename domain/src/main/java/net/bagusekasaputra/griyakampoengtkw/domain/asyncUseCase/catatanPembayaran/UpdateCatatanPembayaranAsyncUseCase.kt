package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class UpdateCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
    private val indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
): AsyncUseCase<UpdateCatatanPembayaranAsyncUseCase.Request, Nothing>() {

    sealed class Request(val catatanType: Int): AsyncUseCase.Request

    data class KavlingRequest(
        val kavling: String,
        val catatanPembayaran: KavlingCatatanPembayaran,
    ): Request(CatatanPembayaran.KAVLING)

    data class IndenBookingRequest(
        val keyId: String,
        val catatanPembayaran: IndenBookingCatatanPembayaran,
    ): Request(CatatanPembayaran.INDEN_BOOKING)

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when (request.catatanType) {
            CatatanPembayaran.KAVLING -> TODO("Not yet implemented")
            CatatanPembayaran.INDEN_BOOKING -> {
                val indenBookingRequest = request as IndenBookingRequest

                flow {
                    emit(indenBookingCatatanPembayaranRepository.update(
                        indenBookingRequest.keyId,
                        indenBookingRequest.catatanPembayaran,
                    ))
                }
            }

            else -> throw UnsupportedOperationException()
        }
    }

}