package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class GetCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
    private val indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
): AsyncUseCase<GetCatatanPembayaranAsyncUseCase.Request, CatatanPembayaran?>() {

    sealed class Request(
        val catatanType: Int,
        val dataMode: DataMode,
    ): AsyncUseCase.Request

    data class KavlingRequest(
        val kavlingKode: String,
        val mDataMode: DataMode,
    ): Request(CatatanPembayaran.KAVLING, mDataMode)

    data class IndenBookingRequest(
        val keyId: String,
        val mDataMode: DataMode,
    ): Request(CatatanPembayaran.INDEN_BOOKING, mDataMode)

    override fun process(request: Request): Flow<Result<CatatanPembayaran?>> {
        return when (request.catatanType) {
            CatatanPembayaran.KAVLING -> {
                val kavlingRequest = request as KavlingRequest

                kavlingCatatanPembayaranRepository.getCatatan(
                    kavlingRequest.kavlingKode,
                    kavlingRequest.dataMode
                )
            }
            CatatanPembayaran.INDEN_BOOKING -> {
                val indenBookingRequest = request as IndenBookingRequest

                flow {
                    emit(indenBookingCatatanPembayaranRepository.get(indenBookingRequest.keyId))
                }
            }
            else -> {
                throw UnsupportedOperationException()
            }
        }
    }
}