package net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddFeeMarketingUseCase @Inject constructor(
    private val feeMarketingRepository: FeeMarketingRepository,
): UseCase<AddFeeMarketingUseCase.Request, AddFeeMarketingUseCase.Response>() {

    data class Request(val feeMarketing: FeeMarketing): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        // We need to use Number Util, because the FeeMarketing mapped here is from UI Layer,
        // which is a comma separated format
        request.feeMarketing.biayaMarketer = NumberUtil
            .formatStringToLong(request.feeMarketing.biayaMarketer)
            .toString()

        return feeMarketingRepository.addFeeMarketing(request.feeMarketing).map {
            Response(it)
        }
    }
}