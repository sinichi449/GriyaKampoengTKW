package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class GetFeeMarketingByKavlingKodeAsyncUseCase(
    private val feeMarketingRepository: FeeMarketingRepository,
): AsyncUseCase<GetFeeMarketingByKavlingKodeAsyncUseCase.Request, FeeMarketing?>() {

    data class Request(val kavlingKode: String, val offline: Boolean): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<FeeMarketing?>> {
        return feeMarketingRepository.getByKavlingKode(request.kavlingKode, request.offline)
    }
}