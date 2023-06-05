package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class InsertPembayaranAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<InsertPembayaranAsyncUseCase.Request, Nothing>() {

    sealed class Request(val pembayaranType: Int): AsyncUseCase.Request

    data class KavlingRequest(
        val kavling: String,
        val pembayaran: Pembayaran,
    ): Request(Pembayaran.PEMBAYARAN_KAVLING)

    data class IndenBookingRequest(
        val keyId: String,
        val pembayaran: Pembayaran,
    ): Request(Pembayaran.PEMBAYARAN_INDEN_BOOKING)

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when (request.pembayaranType) {
            Pembayaran.PEMBAYARAN_KAVLING -> {
                val kavlingRequest = request as KavlingRequest

                flow {
                    val isValidPembayaran = kavlingRequest.pembayaran.validate()
                    if (!isValidPembayaran) {
                        emit(Result.failure(IllegalStateException("Jumlah uang dibayar tidak valid!")))
                    } else {
                        val result = pembayaranRepository.addPembayaran(
                            kavlingKode = kavlingRequest.kavling,
                            pembayaran = kavlingRequest.pembayaran,
                        ).map {
                            if (it.isSuccess) Result.success(null)
                            else Result.failure(it.exceptionOrNull() ?: Exception("Unkown error"))
                        }

                        emitAll(result)
                    }
                }
            }
            Pembayaran.PEMBAYARAN_INDEN_BOOKING -> TODO("Not implemented yet")
            else -> throw UnsupportedOperationException("Jenis pembayaran ${request.pembayaranType} tidak diketahui !")
        }
    }
}