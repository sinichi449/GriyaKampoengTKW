package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class UpdatePembayaranAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<UpdatePembayaranAsyncUseCase.Request, Nothing?>() {

    sealed class Request(val pembayaranType: Int): AsyncUseCase.Request

    data class KavlingRequest(
        val kavling: String,
        val oldPembayaran: Pembayaran,
        val newPembayaran: Pembayaran,
    ): Request(Pembayaran.PEMBAYARAN_KAVLING)

    data class IndenBookingRequest(
        val keyId: String,
        val oldPembayaran: Pembayaran,
        val newPembayaran: Pembayaran,
    ): Request(Pembayaran.PEMBAYARAN_INDEN_BOOKING)

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when (request.pembayaranType) {
            Pembayaran.PEMBAYARAN_KAVLING -> {
                flow {
                    val kavlingRequest = request as KavlingRequest

                    kavlingRequest.apply {
                        // Check old termin and new termin equality
                        val sameTermin = oldPembayaran.termin == newPembayaran.termin
                        if (!sameTermin) {
                            emit(Result.failure(IllegalArgumentException("Termin lama tidak sama dengan termin baru!")))
                        } else {
                            // Check if old pembayaran and new pembayaran are the same
                            val oldAndNewPembayaranTheSame = oldPembayaran.isEqualTo(newPembayaran)
                            if (oldAndNewPembayaranTheSame) {
                                emit(Result.failure(Exception("Sistem tidak mendeteksi adanya perubahan pada Pembayaran ini!")))
                            } else {
                                emit(pembayaranRepository.updatePembayaran(kavling, newPembayaran))
                            }
                        }
                    }
                }
            }
            Pembayaran.PEMBAYARAN_INDEN_BOOKING -> TODO("Not yet implemented")
            else -> throw UnsupportedOperationException("Update pembayaran dengan tipe ${request.pembayaranType} tidak dikenali!")
        }
    }


}