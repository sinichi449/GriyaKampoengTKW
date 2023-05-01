package net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class DeleteFeeMarketingUseCase(
    private val feeMarketingRepository: FeeMarketingRepository,
): UseCase<DeleteFeeMarketingUseCase.Request, DeleteFeeMarketingUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return feeMarketingRepository.deleteFeeMarketing(request.kavlingKode).map {
            Response(it)
        }
    }
}