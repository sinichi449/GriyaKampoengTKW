package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository

class AddCatatanPembayaranAsyncUseCase(
    private val kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
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
                TODO("Not implemented yet")
            }
            else -> throw UnsupportedOperationException()
        }
    }
}