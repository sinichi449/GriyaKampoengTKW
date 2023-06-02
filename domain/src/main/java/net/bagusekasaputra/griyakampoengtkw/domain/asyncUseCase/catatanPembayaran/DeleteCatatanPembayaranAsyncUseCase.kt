package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class DeleteCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
    private val indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
): AsyncUseCase<DeleteCatatanPembayaranAsyncUseCase.Request, Nothing>() {

    sealed class Request(
        val catatanType: Int,
    ): AsyncUseCase.Request

    data class KavlingRequest(
        val kavling: String
    ): Request(CatatanPembayaran.KAVLING)

    data class IndenBookingRequest(
        val keyId: String,
    ): Request(CatatanPembayaran.INDEN_BOOKING)


    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when (request.catatanType) {
            CatatanPembayaran.KAVLING -> {
                val kavlingRequest = request as KavlingRequest

                kavlingCatatanPembayaranRepository.deleteCatatan(kavlingRequest.kavling)
            }
            CatatanPembayaran.INDEN_BOOKING -> {
                val indenBookingRequest = request as IndenBookingRequest

                flow {
                    emit(indenBookingCatatanPembayaranRepository.delete(indenBookingRequest.keyId))
                }
            }
            else -> throw UnsupportedOperationException()
        }
    }

}