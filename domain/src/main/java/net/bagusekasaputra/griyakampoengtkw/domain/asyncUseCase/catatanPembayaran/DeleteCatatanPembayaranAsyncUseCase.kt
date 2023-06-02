package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class DeleteCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
): AsyncUseCase<DeleteCatatanPembayaranAsyncUseCase.Request, Nothing>() {

    sealed class Request(
        val catatanType: Int,
    ): AsyncUseCase.Request

    data class KavlingRequest(
        val mCatatanType: Int,
        val kavling: String
    ): Request(mCatatanType)

    data class IndenBookingRequest(
        val mCatatanType: Int,
        val keyId: String,
    ): Request(mCatatanType)


    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when (request.catatanType) {
            CatatanPembayaran.KAVLING -> {
                val kavlingRequest = request as KavlingRequest
                kavlingCatatanPembayaranRepository.deleteCatatan(kavlingRequest.kavling)
            }
            CatatanPembayaran.INDEN_BOOKING -> {
                TODO("Not yet implemented")
            }
            else -> throw UnsupportedOperationException()
        }
    }

}