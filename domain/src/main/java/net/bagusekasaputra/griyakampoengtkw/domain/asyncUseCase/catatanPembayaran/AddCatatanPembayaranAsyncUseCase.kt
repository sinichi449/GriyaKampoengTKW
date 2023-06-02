package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class AddCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
    private val indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
): AsyncUseCase<AddCatatanPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val catatanType: Int,
        val catatanPembayaran: CatatanPembayaran,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return when(request.catatanType) {
            CatatanPembayaran.KAVLING -> {
                val catatanPembayaran = request.catatanPembayaran as KavlingCatatanPembayaran
                kavlingCatatanPembayaranRepository.addCatatan(
                    catatanPembayaran.kavlingKode,
                    catatanPembayaran,
                )
            }
            CatatanPembayaran.INDEN_BOOKING -> {
                val catatanPembayaran = request.catatanPembayaran as IndenBookingCatatanPembayaran

                flow {
                    emit(indenBookingCatatanPembayaranRepository.insert(catatanPembayaran))
                }
            }
            else -> throw UnsupportedOperationException()
        }
    }
}