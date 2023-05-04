package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository

class SetBaselinePembayaranAsyncUseCase(
    private val baselinePembayaranRepository: BaselinePembayaranRepository
): AsyncUseCase<SetBaselinePembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val baselinePembayaran: BaselinePembayaran,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return baselinePembayaranRepository.insert(request.baselinePembayaran)
    }
}
