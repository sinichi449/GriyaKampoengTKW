package net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class UpdateFeeMarketingUseCase(
    private val feeMarketingRepository: FeeMarketingRepository,
): UseCase<UpdateFeeMarketingUseCase.Request, UpdateFeeMarketingUseCase.Response>() {

    data class Request(val oldFeeMarketing: FeeMarketing, val newFeeMarketing: FeeMarketing): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        // We need to use Number Util, because the FeeMarketing mapped here is from UI Layer,
        // which is a comma separated format
        request.oldFeeMarketing.biayaMarketer = NumberUtil
            .formatStringToLong(request.oldFeeMarketing.biayaMarketer)
            .toString()
        request.newFeeMarketing.biayaMarketer = NumberUtil
            .formatStringToLong(request.newFeeMarketing.biayaMarketer)
            .toString()

        return feeMarketingRepository.updateFeeMarketing(
            request.oldFeeMarketing,
            request.newFeeMarketing,
        ).map {
            Response(it)
        }
    }
}