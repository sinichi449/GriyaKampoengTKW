package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetSinglePembayaranByKavlingAndTerminAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetSinglePembayaranByKavlingAndTerminAsyncUseCase.Request, Pembayaran>() {

    sealed class Request(val pembayaranType: Int, val mTermin: String): AsyncUseCase.Request

    data class KavlingRequest(
        val kavling: String,
        private val termin: String,
    ): Request(Pembayaran.PEMBAYARAN_KAVLING, termin)

    data class IndenBookingRequest(
        val keyId: String,
        private val termin: String,
    ): Request(Pembayaran.PEMBAYARAN_INDEN_BOOKING, termin)

    override fun process(request: Request): Flow<Result<Pembayaran?>> {
        return when (request.pembayaranType) {
            Pembayaran.PEMBAYARAN_KAVLING -> {
                val kavlingRequest = request as KavlingRequest

                flow {
                    emit(pembayaranRepository.getByKavlingAndTermin(
                        kavlingRequest.kavling, kavlingRequest.mTermin)
                    )
                }
            }
            Pembayaran.PEMBAYARAN_INDEN_BOOKING -> TODO("Not yet implemented")
            else -> throw UnsupportedOperationException("Pembayaran dengan kode tipe ${request.pembayaranType} tidak dikenali!")
        }
    }


}