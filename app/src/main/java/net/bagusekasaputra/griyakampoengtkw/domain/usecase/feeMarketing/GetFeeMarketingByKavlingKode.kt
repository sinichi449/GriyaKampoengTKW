package net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFeeMarketingByKavlingKode @Inject constructor(
    private val feeMarketingRepository: FeeMarketingRepository,
): UseCase<GetFeeMarketingByKavlingKode.Request, GetFeeMarketingByKavlingKode.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<FeeMarketing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return feeMarketingRepository.getByKavlingKode(request.kavlingKode).map {
            Response(it)
        }
    }

}