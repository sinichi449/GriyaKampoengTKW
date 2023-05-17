package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository

class GetStatusPembayaranKavlingAsyncUseCase(
    private val statusPembayaranRepository: StatusPembayaranRepository
): AsyncUseCase<GetStatusPembayaranKavlingAsyncUseCase.Request, StatusPembayaran>() {

    data class Request(
        val kavling: String,
        val dataMode: DataMode,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<StatusPembayaran?>> {
        return statusPembayaranRepository.get(request.kavling, request.dataMode)
    }
}