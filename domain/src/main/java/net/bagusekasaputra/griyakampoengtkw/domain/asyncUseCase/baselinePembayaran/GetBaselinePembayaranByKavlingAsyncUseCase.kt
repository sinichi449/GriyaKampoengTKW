package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository

class GetBaselinePembayaranByKavlingAsyncUseCase(
    private val baselinePembayaranRepository: BaselinePembayaranRepository
): AsyncUseCase<GetBaselinePembayaranByKavlingAsyncUseCase.Request, BaselinePembayaran>() {

    data class Request(
        val kavling: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<BaselinePembayaran?>> {
        return baselinePembayaranRepository.get(request.kavling)
    }

}